package hmo.payments.sm.action.impl;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.service.PaymentService;
import hmo.payments.sm.action.PaymentAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class AuthCancel implements PaymentAction {

    @Autowired
    private PaymentService paymentService;

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        Long paymentId = getPaymentId(context.getMessage());

        paymentService.authCancel(paymentId);
    }
}
