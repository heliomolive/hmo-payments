package hmo.payments.sm.action.impl;

import hmo.payments.domain.dto.AuthDto;
import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.enums.AuthState;
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
public class AuthRequestAction implements PaymentAction {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AcquirerService acquirerService;

    @Autowired
    private PaymentStateMachineFactory paymentStateMachineFactory;

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        Long paymentId = getPaymentId(context.getMessage());
        log.info("Executing AuthRequestAction for payment [{}]", paymentId);
        log.info("source={}, target={}", context.getSource(), context.getTarget());

        // 1. Update payment state:
        PaymentDto paymentDto = paymentService.authRequested(paymentId);

        // 2. Call AuthService to execute auth:
        AuthDto authDto = acquirerService.authorization(paymentId);

        // 3. Generate an event for auth result:
        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto);
        sm.sendEvent(AuthState.SUCCESS.equals(authDto.getAuthState()) ?
                PaymentEvent.AUTH_APPROVE : PaymentEvent.AUTH_DECLINE);
    }
}
