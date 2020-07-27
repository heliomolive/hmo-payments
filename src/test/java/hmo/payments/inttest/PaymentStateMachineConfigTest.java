package hmo.payments.inttest;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentStateMachineConfigTest {

    private StateMachine<PaymentState, PaymentEvent> fixture;

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> factory;


    @BeforeEach
    public void beforeEach() {
        fixture = factory.getStateMachine();
        fixture.start();
    }

    @Test
    void testPaymentAuthorizationSuccess() {
        assertEquals(PaymentState.NEW, fixture.getState().getId());

        fixture.sendEvent(PaymentEvent.PRE_AUTH_APPROVE);
        assertEquals(PaymentState.PRE_AUTH_SUCCESS, fixture.getState().getId());

        fixture.sendEvent(PaymentEvent.AUTH_APPROVE);
        assertEquals(PaymentState.AUTH_SUCCESS, fixture.getState().getId());
    }

    @Test
    void testPaymentAuthorizationDeclined() {
        assertEquals(PaymentState.NEW, fixture.getState().getId());

        fixture.sendEvent(PaymentEvent.PRE_AUTH_APPROVE);
        assertEquals(PaymentState.PRE_AUTH_SUCCESS, fixture.getState().getId());

        fixture.sendEvent(PaymentEvent.AUTH_DECLINE);
        assertEquals(PaymentState.AUTH_DECLINED, fixture.getState().getId());
    }

    @Test
    public void testPaymentPreAuthorizationDeclined() {
        assertEquals(PaymentState.NEW, fixture.getState().getId());

        fixture.sendEvent(PaymentEvent.PRE_AUTH_DECLINE);
        assertEquals(PaymentState.PRE_AUTH_DECLINED, fixture.getState().getId());
    }
}