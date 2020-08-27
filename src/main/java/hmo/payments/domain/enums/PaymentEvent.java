package hmo.payments.domain.enums;

public enum PaymentEvent {
    PRE_AUTH_REQUEST,
    PRE_AUTH_APPROVE,
    PRE_AUTH_DECLINE,

    AUTH_REQUEST,
    AUTH_APPROVE,
    AUTH_DECLINE,

    PAYMENT_SETTLEMENT,
    PAYMENT_CANCEL,
    PAYMENT_VOIDED;
}
