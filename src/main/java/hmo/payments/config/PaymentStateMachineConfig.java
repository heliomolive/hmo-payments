package hmo.payments.config;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.sm.action.PaymentAction;
import hmo.payments.sm.action.PaymentActionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class PaymentStateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Autowired
    private PaymentActionFactory paymentActionFactory;

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH_SETTLED)
                .end(PaymentState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {

        configureStateTransition(transitions,
                PaymentState.NEW, PaymentEvent.PRE_AUTH_APPROVE, PaymentState.PRE_AUTH_SUCCESS);
        configureStateTransition(transitions,
                PaymentState.NEW, PaymentEvent.PRE_AUTH_DECLINE, PaymentState.PRE_AUTH_DECLINED);
        configureStateTransition(transitions,
                PaymentState.NEW, PaymentEvent.PAYMENT_CANCEL, PaymentState.CANCELLED);

        configureStateTransition(transitions,
                PaymentState.PRE_AUTH_SUCCESS, PaymentEvent.AUTH_APPROVE, PaymentState.AUTH_SUCCESS);
        configureStateTransition(transitions,
                PaymentState.PRE_AUTH_SUCCESS, PaymentEvent.AUTH_DECLINE, PaymentState.AUTH_DECLINED);
        configureStateTransition(transitions,
                PaymentState.PRE_AUTH_SUCCESS, PaymentEvent.PRE_AUTH_CANCEL, PaymentState.NEW);
        configureStateTransition(transitions,
                PaymentState.PRE_AUTH_SUCCESS, PaymentEvent.PAYMENT_CANCEL, PaymentState.CANCELLED);

        configureStateTransition(transitions,
                PaymentState.PRE_AUTH_DECLINED, PaymentEvent.PRE_AUTH_APPROVE, PaymentState.PRE_AUTH_SUCCESS);
        configureStateTransition(transitions,
                PaymentState.PRE_AUTH_DECLINED, PaymentEvent.PAYMENT_CANCEL, PaymentState.CANCELLED);

        configureStateTransition(transitions,
                PaymentState.AUTH_SUCCESS, PaymentEvent.AUTH_CANCEL, PaymentState.PRE_AUTH_SUCCESS);
        configureStateTransition(transitions,
                PaymentState.AUTH_SUCCESS, PaymentEvent.AUTH_SETTLEMENT, PaymentState.AUTH_SETTLED);
        configureStateTransition(transitions,
                PaymentState.AUTH_SUCCESS, PaymentEvent.PAYMENT_CANCEL, PaymentState.CANCELLED);

        configureStateTransition(transitions,
                PaymentState.AUTH_DECLINED, PaymentEvent.PAYMENT_CANCEL, PaymentState.CANCELLED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> listenerAdapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(format("State changed from [%s] to [%s]",
                        nonNull(from) ? from.getId() : null,
                        nonNull(to) ? to.getId() : null));
            }
        };

        config.withConfiguration().listener(listenerAdapter);
    }

    private void configureStateTransition(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions,
                                          PaymentState sourceState, PaymentEvent paymentEvent, PaymentState targetState)
            throws Exception {
        transitions.withExternal()
                .source(sourceState)
                .event(paymentEvent)
                .target(targetState)
                .action(actionFor(paymentEvent));
    }

    private Action<PaymentState, PaymentEvent> actionFor(PaymentEvent paymentEvent) {
        return context -> {
            PaymentAction paymentAction = paymentActionFactory.getPaymentAction(paymentEvent);
            log.info("Starting action [{}] for event [{}]...",
                    paymentAction.getClass().getSimpleName(), paymentEvent.name());
            paymentAction.execute(context);

        };
    }

}
