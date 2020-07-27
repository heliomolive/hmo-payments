package hmo.payments.sm.action.impl;

import hmo.payments.service.PaymentService;
import hmo.payments.sm.action.PaymentAction;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreAuthApproveAction implements PaymentAction {

    @Autowired
    private PaymentService paymentService;

    @Override
    @Transactional
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        Long paymentId = getPaymentId(context.getMessage());

        paymentService.preAuthApproved(paymentId);
    }
}
