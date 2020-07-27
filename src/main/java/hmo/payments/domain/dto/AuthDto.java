package hmo.payments.domain.dto;

import hmo.payments.domain.enums.AuthState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {

    private Long authId;
    private Long paymentId;
    private AuthState authState;
}
