package hmo.payments.repository;

import hmo.payments.repository.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, CustomPaymentRepository {
}
