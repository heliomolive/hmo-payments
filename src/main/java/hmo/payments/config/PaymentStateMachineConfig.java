package hmo.payments.config;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.sm.action.impl.AuthApproveAction;
import hmo.payments.sm.action.impl.AuthDeclineAction;
import hmo.payments.sm.action.impl.AuthRequestAction;
import hmo.payments.sm.action.impl.PaymentSettlementAction;
import hmo.payments.sm.action.impl.PaymentCancelAction;
import hmo.payments.sm.action.impl.PaymentCancelRequestAction;
import hmo.payments.sm.action.impl.PreAuthApproveAction;
import hmo.payments.sm.action.impl.PreAuthDeclineAction;
import hmo.payments.sm.action.impl.PreAuthRequestAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.Set;

import static java.util.Objects.nonNull;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class PaymentStateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Autowired
    private AuthRequestAction authRequestAction;

    @Autowired
    private AuthApproveAction authApproveAction;

    @Autowired
    private AuthDeclineAction authDeclineAction;

    @Autowired
    private PreAuthRequestAction preAuthRequestAction;

    @Autowired
    private PreAuthApproveAction preAuthApproveAction;

    @Autowired
    private PreAuthDeclineAction preAuthDeclineAction;

    @Autowired
    private PaymentCancelAction paymentCancelAction;

    @Autowired
    private PaymentCancelRequestAction paymentCancelRequestAction;

    @Autowired
    private PaymentSettlementAction paymentSettlementAction;


    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates() //First level states
                .initial(PaymentState.NEW)
                .end(PaymentState.SETTLED)
                .end(PaymentState.CANCELLED)
                .state(PaymentState.CANCEL_REQUESTED, ctx -> paymentCancelRequestAction.execute(ctx))
                .state(PaymentState.CANCELLED, ctx -> paymentCancelAction.execute(ctx))
                .state(PaymentState.SETTLED, ctx -> paymentSettlementAction.execute(ctx))
                .states(Set.of(PaymentState.PAYMENT_REGISTERED, PaymentState.PAYMENT_IN_PROGRESS))

                .and().withStates() //Second level states inside PAYMENT_REGISTERED
                .parent(PaymentState.PAYMENT_REGISTERED)
                .initial(PaymentState.PRE_AUTH_REQUESTED)
                .state(PaymentState.PRE_AUTH_REQUESTED, ctx -> preAuthRequestAction.execute(ctx))
                .state(PaymentState.PRE_AUTH_DECLINED, ctx -> preAuthDeclineAction.execute(ctx))

                .and().withStates() //Second level states inside PAYMENT_IN_PROGRESS
                .parent(PaymentState.PAYMENT_IN_PROGRESS)
                .initial(PaymentState.PRE_AUTH_SUCCESS)
                .state(PaymentState.PRE_AUTH_SUCCESS, ctx -> preAuthApproveAction.execute(ctx))
                .state(PaymentState.AUTH_REQUESTED, ctx -> authRequestAction.execute(ctx))
                .state(PaymentState.AUTH_SUCCESS, ctx -> authApproveAction.execute(ctx))
                .state(PaymentState.AUTH_DECLINED, ctx -> authDeclineAction.execute(ctx))
        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {

        transitions.withExternal().source(PaymentState.NEW).target(PaymentState.PAYMENT_REGISTERED)
                .event(PaymentEvent.PRE_AUTH_REQUEST);

        // Superstate PAYMENT_REGISTERED:
        transitions.withExternal().source(PaymentState.PRE_AUTH_REQUESTED).target(PaymentState.PAYMENT_IN_PROGRESS)
                .event(PaymentEvent.PRE_AUTH_APPROVE);
        transitions.withExternal().source(PaymentState.PRE_AUTH_REQUESTED).target(PaymentState.PRE_AUTH_DECLINED)
                .event(PaymentEvent.PRE_AUTH_DECLINE);
        transitions.withExternal().source(PaymentState.PRE_AUTH_DECLINED).target(PaymentState.PRE_AUTH_REQUESTED)
                .event(PaymentEvent.PRE_AUTH_REQUEST);
        transitions.withExternal().source(PaymentState.PAYMENT_REGISTERED).target(PaymentState.CANCELLED)
                .event(PaymentEvent.PAYMENT_CANCEL);

        // Superstate PAYMENT_IN_PROGRESS:
        transitions.withExternal().source(PaymentState.PRE_AUTH_SUCCESS).target(PaymentState.AUTH_REQUESTED)
                .event(PaymentEvent.AUTH_REQUEST);
        transitions.withExternal().source(PaymentState.AUTH_REQUESTED).target(PaymentState.AUTH_SUCCESS)
                .event(PaymentEvent.AUTH_APPROVE);
        transitions.withExternal().source(PaymentState.AUTH_REQUESTED).target(PaymentState.AUTH_DECLINED)
                .event(PaymentEvent.AUTH_DECLINE);
        transitions.withExternal().source(PaymentState.AUTH_SUCCESS).target(PaymentState.SETTLED)
                .event(PaymentEvent.PAYMENT_SETTLEMENT);
        transitions.withExternal().source(PaymentState.AUTH_DECLINED).target(PaymentState.AUTH_REQUESTED)
                .event(PaymentEvent.AUTH_REQUEST);
        transitions.withExternal().source(PaymentState.PAYMENT_IN_PROGRESS).target(PaymentState.CANCEL_REQUESTED)
                .event(PaymentEvent.PAYMENT_CANCEL);

        transitions.withExternal().source(PaymentState.CANCEL_REQUESTED).target(PaymentState.CANCELLED)
                .event(PaymentEvent.PAYMENT_VOIDED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> listenerAdapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                if (nonNull(from)) { //Ignore state changes generated by State Machine starting (i.e, fromState is null)
                    log.info("State changed from [{}] to [{}]", from.getId(), to.getId());
                }
            }
        };
        config.withConfiguration().listener(listenerAdapter);
    }
}
