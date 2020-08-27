package hmo.payments.service.impl;

import hmo.payments.domain.dto.AuthDto;
import hmo.payments.domain.dto.PreAuthDto;
import hmo.payments.domain.enums.AuthState;
import hmo.payments.domain.enums.PreAuthState;
import hmo.payments.domain.exception.UnprocessableEntityException;
import hmo.payments.domain.mapper.AuthMapper;
import hmo.payments.domain.mapper.PreAuthMapper;
import hmo.payments.repository.AuthRepository;
import hmo.payments.repository.PreAuthRepository;
import hmo.payments.repository.entity.Auth;
import hmo.payments.repository.entity.Payment;
import hmo.payments.repository.entity.PreAuth;
import hmo.payments.service.AcquirerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static java.lang.String.format;

@Log4j2
@Service
public class AcquirerServiceImpl implements AcquirerService {

    @Autowired
    private PreAuthRepository preAuthRepository;

    @Autowired
    private PreAuthMapper preAuthMapper;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthMapper authMapper;

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
    public AuthDto authorization(Long paymentId) {
        log.info("Simulating auth for payment [{}]", paymentId);

        Auth auth = authRepository.save(
                Auth.builder()
                        .authState(paymentId%2 == 1 ? AuthState.SUCCESS : AuthState.DECLINED)
                        .payment(Payment.builder().paymentId(paymentId).build())
                        .build());

        return authMapper.getAuthDto(auth);
    }

    @Override
    public void voidPayment(Long paymentId) {
        log.info("Simulating void for payment [{}]", paymentId);

        preAuthRepository.findByPaymentPaymentId(paymentId).ifPresentOrElse(
                preAuth -> {
                    if (PreAuthState.SUCCESS.equals(preAuth.getPreAuthState())) {
                        preAuth.setPreAuthState(PreAuthState.VOIDED);
                        preAuthRepository.save(preAuth);
                        log.info("Pre-auth [{}] voided", preAuth.getPreAuthId());
                    }
                },
                () -> {
                    throw new UnprocessableEntityException(
                        format("No pre-auth found to be voided for payment [%d]", paymentId));
                }
        );

        authRepository.findByPaymentPaymentId(paymentId).ifPresent(auth -> {
            if (AuthState.SUCCESS.equals(auth.getAuthState())) {
                auth.setAuthState(AuthState.VOIDED);
                authRepository.save(auth);
                log.info("Auth [{}] voided", auth.getAuthId());
            }
        });
    }
}
