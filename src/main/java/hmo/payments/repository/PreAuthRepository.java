package hmo.payments.repository;

import hmo.payments.repository.entity.PreAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreAuthRepository extends JpaRepository<PreAuth, Long> {
}
