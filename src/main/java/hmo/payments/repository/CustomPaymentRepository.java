package hmo.payments.repository;

import hmo.payments.repository.entity.Payment;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CustomPaymentRepository {

    @Transactional
    Optional<Payment> findByIdWithFetchDependence(Long paymentId);
}
