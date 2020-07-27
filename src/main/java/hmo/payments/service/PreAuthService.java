package hmo.payments.service;

import hmo.payments.domain.dto.PreAuthDto;

import java.math.BigDecimal;

public interface PreAuthService {

    PreAuthDto preAuthorization(Long paymentId, BigDecimal amount);

    PreAuthDto cancelPreAuthorization(Long preAuthId);

}
