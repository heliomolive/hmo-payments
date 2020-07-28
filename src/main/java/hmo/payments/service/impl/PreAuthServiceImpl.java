package hmo.payments.service.impl;

import hmo.payments.domain.dto.PreAuthDto;
import hmo.payments.domain.enums.PreAuthState;
import hmo.payments.domain.exception.NotFoundException;
import hmo.payments.domain.exception.UnprocessableEntityException;
import hmo.payments.domain.mapper.PreAuthMapper;
import hmo.payments.repository.PreAuthRepository;
import hmo.payments.repository.entity.Payment;
import hmo.payments.repository.entity.PreAuth;
import hmo.payments.service.PreAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static java.lang.String.format;

@Slf4j
@Service
public class PreAuthServiceImpl implements PreAuthService {

    @Autowired
    private PreAuthRepository preAuthRepository;

    @Autowired
    private PreAuthMapper preAuthMapper;

    @Override
    @Transactional
    public PreAuthDto preAuthorization(Long paymentId, BigDecimal amount) {
        log.info("Simulating pre-auth for payment [{}] with value [{}]", paymentId, amount);

        PreAuthState preAuthState;

        if (amount.compareTo(new BigDecimal(50))<=0) {
            log.info("Payment [{}] pre-auth success", paymentId);
            preAuthState = PreAuthState.SUCCESS;
        } else {
            log.info("Payment [{}] pre-auth declined", paymentId);
            preAuthState = PreAuthState.DECLINED;
        }
        PreAuth preAuth = preAuthRepository.save(
                PreAuth.builder()
                        .payment(Payment.builder().paymentId(paymentId).build())
                        .preAuthState(preAuthState).build());
        return preAuthMapper.getPreAuthDto(preAuth);
    }

    @Override
    @Transactional
    public PreAuthDto cancelPreAuthorization(Long preAuthId) {
        log.info("Simulating cancel for preAuth [{}]", preAuthId);

        PreAuth preAuth = preAuthRepository.findById(preAuthId)
                .orElseThrow(() -> new NotFoundException(format("Invalid preAuthId [%d]", preAuthId)));

        if (!PreAuthState.SUCCESS.equals(preAuth.getPreAuthState())) {
            throw new UnprocessableEntityException(
                    format("Can not cancel pre-auth [%d], invalid state [%s]", preAuthId, preAuth.getPreAuthState()));
        }

        preAuth.setPreAuthState(PreAuthState.CANCELLED);

        return preAuthMapper.getPreAuthDto(
                preAuthRepository.save(preAuth));
    }

}
