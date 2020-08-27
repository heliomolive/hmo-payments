package hmo.payments.sm.action;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.domain.exception.UnprocessableEntityException;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.math.BigDecimal;
import java.util.Optional;

import static hmo.payments.sm.PaymentStateMachine.PAYMENT_ID_HEADER;
import static hmo.payments.sm.PaymentStateMachine.AMOUNT;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

public interface PaymentAction extends Action<PaymentState, PaymentEvent> {

    default Long getPaymentId(Message<PaymentEvent> message) {
        Object obj = message.getHeaders().get(PAYMENT_ID_HEADER);
        if (nonNull(obj)) {
            return (long) obj;
        } else {
            throw new UnprocessableEntityException(format(
                    "Invalid message [%s]! %s header required.", message.getPayload(), PAYMENT_ID_HEADER));
        }
    }

    default Optional<BigDecimal> getPaymentAmount(StateContext<PaymentState, PaymentEvent> context) {
        return Optional.ofNullable((BigDecimal) context.getExtendedState().getVariables().get(AMOUNT));
    }
}
