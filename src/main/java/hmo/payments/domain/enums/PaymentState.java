package hmo.payments.domain.enums;

public enum PaymentState {
    NEW                 ("New payment created"),
    PRE_AUTH_SUCCESS    ("Payment successfully pre-authorized"),
    PRE_AUTH_DECLINED   ("Payment with pre-authorization declined or error"),
    AUTH_SUCCESS        ("Payment successfully authorized"),
    AUTH_DECLINED       ("Payment with authorization declined or error"),
    CANCELLED           ("Payment cancelled");

    private String description;

    PaymentState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
