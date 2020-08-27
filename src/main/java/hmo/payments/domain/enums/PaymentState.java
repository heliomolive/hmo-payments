package hmo.payments.domain.enums;

import java.util.Set;

public enum PaymentState {
    NEW                 ("New payment created"),
    PRE_AUTH_REQUESTED  ("Pre-authorization requested"),
    PRE_AUTH_SUCCESS    ("Payment successfully pre-authorized"),
    PRE_AUTH_DECLINED   ("Payment with pre-authorization declined or error"),
    AUTH_REQUESTED      ("Authorization requested"),
    AUTH_SUCCESS        ("Payment successfully authorized"),
    AUTH_DECLINED       ("Payment with authorization declined or error"),
    SETTLED             ("Payment settled"),
    CANCEL_REQUESTED    ("Cancel requested but not executed yet"),
    CANCELLED           ("Payment cancelled"),

    PAYMENT_REGISTERED  ("Superstate for a payment received"),
    PAYMENT_IN_PROGRESS ("Superstate for a payment with pre-auth or auth done or in progress, " +
                                    "but not settled yet");

    private static Set<PaymentState> paymentRegisteredSubStates = Set.of(PRE_AUTH_REQUESTED, PRE_AUTH_DECLINED);
    private static Set<PaymentState> paymentInProgressSubStates =
            Set.of(PRE_AUTH_SUCCESS, AUTH_REQUESTED, AUTH_SUCCESS, AUTH_DECLINED);

    private String description;

    PaymentState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPaymentRegistered() {
        return paymentRegisteredSubStates.contains(this);
    }

    public boolean isPaymentInProgress() {
        return paymentInProgressSubStates.contains(this);
    }
}
