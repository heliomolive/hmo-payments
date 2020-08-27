package hmo.payments.sm.action.impl;

import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.dto.PreAuthDto;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.domain.enums.PreAuthState;
import hmo.payments.domain.exception.UnprocessableEntityException;
import hmo.payments.service.AcquirerService;
import hmo.payments.service.PaymentService;
import hmo.payments.sm.PaymentStateMachine;
import hmo.payments.sm.PaymentStateMachineFactory;
import hmo.payments.sm.action.PaymentAction;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Log4j2
@Component
public class PreAuthRequestAction implements PaymentAction {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AcquirerService acquirerService;

    @Autowired
    private PaymentStateMachineFactory paymentStateMachineFactory;

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        Long paymentId = getPaymentId(context.getMessage());
        BigDecimal amount = getPaymentAmount(context).orElseThrow(
                () -> new UnprocessableEntityException("Can not process pre-auth, amount not declared!"));

        log.info("Executing PreAuthRequestAction for payment [{}] with amount [{}]", paymentId, amount);
        log.info("source={}, target={}", context.getSource(), context.getTarget());

        //1. Update Payment state:
        PaymentDto paymentDto = paymentService.preAuthRequested(paymentId);

        //2. Call PreAuthService to execute pre-auth:
        PreAuthDto preAuthDto = acquirerService.preAuthorization(paymentId, amount);

        //3. Generate an event for pre-auth result:
        PaymentStateMachine sm = paymentStateMachineFactory.create(paymentDto);
        sm.sendEvent(Objects.equals(preAuthDto.getPreAuthState(), PreAuthState.SUCCESS) ?
                PaymentEvent.PRE_AUTH_APPROVE : PaymentEvent.PRE_AUTH_DECLINE);
    }
}
