package hmo.payments.sm;

import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.service.PaymentService;
import hmo.payments.service.PaymentStateMachineInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;

@AllArgsConstructor
public class PaymentStateMachine {

    public static final String PAYMENT_ID_HEADER = "PaymentId";

    private StateMachine<PaymentState, PaymentEvent> sm;
    private Long paymentId;
    private PaymentStateMachineInterceptor paymentStateMachineInterceptor;
    private PaymentService paymentService;

    public PaymentState sendEvent(PaymentEvent paymentEvent) {

        PaymentDto paymentDto = paymentService.findPaymentById(paymentId);
        resetStateMachine(paymentDto.getState());

        sm.sendEvent(MessageBuilder
                .withPayload(paymentEvent)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build());

        return sm.getState().getId();
    }

    private void resetStateMachine(PaymentState currentState) {
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(paymentStateMachineInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(currentState, null,
                    null, null));
        });
        sm.start();
    }
}
