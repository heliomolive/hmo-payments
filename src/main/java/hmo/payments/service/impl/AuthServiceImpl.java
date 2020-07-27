package hmo.payments.service.impl;

import hmo.payments.domain.dto.AuthDto;
import hmo.payments.domain.enums.AuthState;
import hmo.payments.domain.exception.NotFoundException;
import hmo.payments.domain.exception.UnprocessableEntityException;
import hmo.payments.domain.mapper.AuthMapper;
import hmo.payments.repository.AuthRepository;
import hmo.payments.repository.entity.Auth;
import hmo.payments.repository.entity.Payment;
import hmo.payments.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthMapper authMapper;

    public AuthDto authorization(Long paymentId) {
        log.info("Simulating auth for payment [{}]", paymentId);

        Auth auth = authRepository.save(
                Auth.builder()
                        .authState(paymentId%2 == 1 ? AuthState.SUCCESS : AuthState.DECLINED)
                        .payment(Payment.builder().paymentId(paymentId).build())
                        .build());

        return authMapper.getAuthDto(auth);
    }

    public AuthDto cancelAuthorization(Long authId) {
        log.info("Simulating cancellation of auth [{}]", authId);

        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new NotFoundException(format("Auth [%d] not found", authId)));

        if (!AuthState.SUCCESS.equals(auth.getAuthState())) {
            throw new UnprocessableEntityException(
                    format("Can not cancel auth [%d], invalid state [%s]", authId, auth.getAuthState()));
        }

        auth.setAuthState(AuthState.CANCELLED);
        return authMapper.getAuthDto(authRepository.save(auth));
    }
}
