package hmo.payments.service;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.repository.PaymentRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import static hmo.payments.sm.PaymentStateMachine.PAYMENT_ID_HEADER;
import static java.util.Objects.isNull;

@Slf4j
@Builder
@Component
public class PaymentStateMachineInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine) {
        if (isNull(message) || isNull(message.getHeaders().get(PAYMENT_ID_HEADER))) {
            log.warn("Invalid State Change Message will be ignored: {}",
                    isNull(message) ? "Message is null" : PAYMENT_ID_HEADER+" is null");
            return;
        }

        long paymentId = (long) message.getHeaders().get(PAYMENT_ID_HEADER);
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            payment.setState(state.getId());
            paymentRepository.save(payment);
        });
    }
}
