package hmo.payments.service;

import hmo.payments.domain.dto.AuthDto;
import hmo.payments.domain.dto.PreAuthDto;

import java.math.BigDecimal;

public interface AcquirerService {

    PreAuthDto preAuthorization(Long paymentId, BigDecimal amount);

    AuthDto authorization(Long paymentId);

    void voidPayment(Long paymentId);
}
