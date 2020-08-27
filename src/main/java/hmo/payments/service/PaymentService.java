package hmo.payments.service;

import hmo.payments.domain.dto.PaymentDto;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentDto createNewPayment(BigDecimal amount);

    PaymentDto findPaymentById(Long paymentId);

    PaymentDto findPaymentByIdWithFetchDependencies(Long paymentId);

    PaymentDto preAuthRequested(Long paymentId);

    PaymentDto preAuthApproved(Long paymentId);

    PaymentDto preAuthDeclined(Long paymentId);

    PaymentDto authRequested(Long paymentId);

    PaymentDto authApproved(Long paymentId);

    PaymentDto authDeclined(Long paymentId);

    PaymentDto paymentSettled(Long paymentId);

    PaymentDto paymentCancelRequested(Long paymentId);

    PaymentDto paymentCancelled(Long paymentId);
}
