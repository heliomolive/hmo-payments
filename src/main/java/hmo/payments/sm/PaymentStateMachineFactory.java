package hmo.payments.sm;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.service.PaymentService;
import hmo.payments.service.PaymentStateMachineInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentStateMachineFactory {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    @Autowired
    private PaymentStateMachineInterceptor paymentStateMachineInterceptor;

    @Autowired
    protected PaymentService paymentService;

    public PaymentStateMachine create(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm =
                stateMachineFactory.getStateMachine(String.valueOf(paymentId));

        return new PaymentStateMachine(sm, paymentId, paymentStateMachineInterceptor, paymentService);
    }

}
