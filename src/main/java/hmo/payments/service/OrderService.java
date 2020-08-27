package hmo.payments.service;

import hmo.payments.domain.dto.PaymentDto;

import java.math.BigDecimal;

public interface OrderService {

    PaymentDto createAndPreAuthorizePaymentOrder(BigDecimal amount);

    PaymentDto authorizePaymentOrder(Long paymentId);

    PaymentDto findPaymentOrder(Long paymentId);

    PaymentDto settlePaymentOrder(Long paymentId);

    PaymentDto cancelPaymentOrder(Long paymentId);
}
