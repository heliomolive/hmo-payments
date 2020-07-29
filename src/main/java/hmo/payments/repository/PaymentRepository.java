package hmo.payments.repository;

import hmo.payments.repository.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p " +
            "LEFT JOIN FETCH p.preAuthSet pa " +
            "LEFT JOIN FETCH p.authSet a WHERE p.paymentId = :paymentId")
    Optional<Payment> findByIdWithFetchDependence(Long paymentId);
}
