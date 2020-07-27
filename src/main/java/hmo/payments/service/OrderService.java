package hmo.payments.service;

import hmo.payments.domain.dto.PaymentDto;

import java.math.BigDecimal;

public interface OrderService {

    PaymentDto createAndPreAuthorizePaymentOrder(BigDecimal amount);

    PaymentDto authorizePaymentOrder(long paymentId);

    PaymentDto findPaymentOrder(long paymentId);

    PaymentDto cancelPreAuthorization(long preAuthId);

    PaymentDto cancelAuthorization(long authId);
}
