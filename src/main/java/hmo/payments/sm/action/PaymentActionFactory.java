package hmo.payments.sm.action;

import hmo.payments.domain.enums.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentActionFactory {

    @Autowired
    private PaymentAction authApproveAction;

    @Autowired
    private PaymentAction authDeclineAction;

    @Autowired
    private PaymentAction preAuthApproveAction;

    @Autowired
    private PaymentAction preAuthDeclineAction;

    public PaymentAction getPaymentAction(PaymentEvent paymentEvent) {
        switch (paymentEvent) {
            case PRE_AUTH_APPROVE: return preAuthApproveAction;
            case PRE_AUTH_DECLINE: return preAuthDeclineAction;
            case AUTH_APPROVE: return authApproveAction;
            case AUTH_DECLINE: return authDeclineAction;
            default: return null;
        }
    }
}
