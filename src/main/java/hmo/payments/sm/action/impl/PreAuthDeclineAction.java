package hmo.payments.sm.action.impl;

import hmo.payments.service.PaymentService;
import hmo.payments.sm.action.PaymentAction;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PreAuthDeclineAction implements PaymentAction {

    @Autowired
    private PaymentService paymentService;

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        Long paymentId = getPaymentId(context.getMessage());
        log.info("Executing PreAuthDeclineAction for payment [{}]", paymentId);

        paymentService.preAuthDeclined(paymentId);
    }
}
