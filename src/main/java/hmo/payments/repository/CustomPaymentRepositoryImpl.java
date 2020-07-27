package hmo.payments.repository;

import hmo.payments.repository.entity.Payment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;

@Log4j2
@Repository
public class CustomPaymentRepositoryImpl implements CustomPaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Optional<Payment> findByIdWithFetchDependence(Long paymentId) {
        if (entityManager.isJoinedToTransaction()) {
            entityManager.flush();
        }
        entityManager.clear();
        Query query = entityManager.createQuery(
                "SELECT p FROM Payment p " +
                "LEFT JOIN FETCH p.preAuthSet pa " +
                "LEFT JOIN FETCH p.authSet a WHERE p.paymentId = :paymentId");
        query.setParameter("paymentId", paymentId);
        try {
            return Optional.of((Payment)query.getSingleResult());
        } catch (NoResultException e) {
            log.debug("No payment found for ID [{}]", paymentId);
            return Optional.empty();
        }
    }
}
