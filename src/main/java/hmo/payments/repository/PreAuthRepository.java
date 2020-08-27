package hmo.payments.repository;

import hmo.payments.repository.entity.PreAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreAuthRepository extends JpaRepository<PreAuth, Long> {

    Optional<PreAuth> findByPaymentPaymentId(Long paymentId);
}
