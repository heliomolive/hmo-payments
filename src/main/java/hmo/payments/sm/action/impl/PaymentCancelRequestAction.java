package hmo.payments.sm.action.impl;

import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.service.AcquirerService;
import hmo.payments.service.PaymentService;
import hmo.payments.sm.PaymentStateMachine;
import hmo.payments.sm.PaymentStateMachineFactory;
import hmo.payments.sm.action.PaymentAction;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PaymentCancelRequestAction implements PaymentAction {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AcquirerService acquirerService;

    @Autowired
    private PaymentStateMachineFactory paymentStateMachineFactory;

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {

        Long paymentId = getPaymentId(context.getMessage());
        log.info("Executing PaymentCancelRequestAction for payment [{}]", paymentId);
        log.info("source={}, target={}", context.getSource(), context.getTarget());

        // 1. Update payment state:
        PaymentDto paymentDto = paymentService.paymentCancelRequested(paymentId);

        // 2. Call AcquirerService to void payment:
        acquirerService.voidPayment(paymentId);

        // 3. Generate an event for payment voided:
        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto);
        sm.sendEvent(PaymentEvent.PAYMENT_VOIDED);
    }
}
