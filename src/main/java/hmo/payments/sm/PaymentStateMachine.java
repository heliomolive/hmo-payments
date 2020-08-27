package hmo.payments.sm;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import org.springframework.data.util.Pair;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;

public class PaymentStateMachine {

    public static final String PAYMENT_ID_HEADER = "paymentId";
    public static final String AMOUNT = "amount";

    private StateMachine<PaymentState, PaymentEvent> sm;
    private Long paymentId;

    public PaymentStateMachine(StateMachine<PaymentState, PaymentEvent> sm, Long paymentId) {
        this.sm = sm;
        this.paymentId = paymentId;
    }

    public PaymentState sendEvent(PaymentEvent paymentEvent, Pair<Object, Object> ... params) {
        for (Pair<Object, Object> p : params) {
            sm.getExtendedState().getVariables().put(p.getFirst(), p.getSecond());
        }

        sm.sendEvent(MessageBuilder
                .withPayload(paymentEvent)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build());

        return sm.getState().getId();
    }
}
