package hmo.payments.service;

import hmo.payments.domain.dto.PaymentDto;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentDto createNewPayment(BigDecimal amount);

    PaymentDto findPaymentById(Long paymentId);

    PaymentDto findPaymentByIdWithFetchDependencies(Long paymentId);

    void preAuthApproved(Long paymentId);

    void preAuthDeclined(Long paymentId);

    void preAuthCancelled(Long paymentId);

    void authApproved(Long paymentId);

    void authDeclined(Long paymentId);

    void authCancel(Long paymentId);

    void paymentCancel(Long paymentId);
}
