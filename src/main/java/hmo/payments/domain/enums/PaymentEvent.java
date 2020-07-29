package hmo.payments.domain.enums;

public enum PaymentEvent {
    PRE_AUTH_APPROVE,
    PRE_AUTH_DECLINE,
    PRE_AUTH_CANCEL,
    AUTH_APPROVE,
    AUTH_DECLINE,
    AUTH_CANCEL,
    AUTH_SETTLEMENT,
    PAYMENT_CANCEL
}
