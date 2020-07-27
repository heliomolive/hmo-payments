package hmo.payments.service;

import hmo.payments.domain.dto.AuthDto;

public interface AuthService {

    AuthDto authorization(Long paymentId);

    AuthDto cancelAuthorization(Long authId);

}
