package hmo.payments.sm;

import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.service.PaymentService;
import hmo.payments.service.PaymentStateMachineInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class PaymentStateMachineFactory {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    @Autowired
    private PaymentStateMachineInterceptor paymentStateMachineInterceptor;

    @Autowired
    protected PaymentService paymentService;

    public PaymentStateMachine create(PaymentDto paymentDto) {
        StateMachine<PaymentState, PaymentEvent> sm =
                stateMachineFactory.getStateMachine(String.valueOf(paymentDto.getPaymentId()));

        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(paymentStateMachineInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(paymentDto.getState(), null,
                    null, null));
        });

        if (nonNull(paymentDto.getAmount())) {
            sm.getExtendedState().getVariables().put(PaymentStateMachine.AMOUNT, paymentDto.getAmount());
        }

        sm.start();

        return new PaymentStateMachine(sm, paymentDto.getPaymentId());
    }

}
