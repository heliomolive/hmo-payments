package hmo.payments.sm.action;

import hmo.payments.domain.enums.PaymentEvent;
import hmo.payments.domain.enums.PaymentState;
import hmo.payments.sm.action.impl.AuthApproveAction;
import hmo.payments.sm.action.impl.AuthDeclineAction;
import hmo.payments.sm.action.impl.AuthRequestAction;
import hmo.payments.sm.action.impl.PaymentCancelAction;
import hmo.payments.sm.action.impl.PaymentCancelRequestAction;
import hmo.payments.sm.action.impl.PreAuthApproveAction;
import hmo.payments.sm.action.impl.PreAuthDeclineAction;
import hmo.payments.sm.action.impl.PreAuthRequestAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentActionFactory {

    @Autowired
    private AuthRequestAction authRequestAction;

    @Autowired
    private AuthApproveAction authApproveAction;

    @Autowired
    private AuthDeclineAction authDeclineAction;

    @Autowired
    private PreAuthRequestAction preAuthRequestAction;

    @Autowired
    private PreAuthApproveAction preAuthApproveAction;

    @Autowired
    private PreAuthDeclineAction preAuthDeclineAction;

    @Autowired
    private PaymentCancelAction paymentCancelAction;

    @Autowired
    private PaymentCancelRequestAction paymentCancelRequestAction;


    public Optional<PaymentAction> getTransitionAction(PaymentEvent paymentEvent) {
        switch (paymentEvent) {
            case PRE_AUTH_APPROVE:  return Optional.of(preAuthApproveAction);
            case PRE_AUTH_DECLINE:  return Optional.of(preAuthDeclineAction);
            case AUTH_APPROVE:      return Optional.of(authApproveAction);
            case AUTH_DECLINE:      return Optional.of(authDeclineAction);
            default: return Optional.empty();
        }
    }

    public Optional<PaymentAction> getStateAction(PaymentState paymentState) {
        switch (paymentState) {
            case PRE_AUTH_REQUESTED:    return Optional.of(preAuthRequestAction);
            case AUTH_REQUESTED:        return Optional.of(authRequestAction);
            default: return Optional.empty();
        }
    }

}
