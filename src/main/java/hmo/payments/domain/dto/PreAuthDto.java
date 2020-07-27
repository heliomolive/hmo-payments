package hmo.payments.domain.dto;

import hmo.payments.domain.enums.PreAuthState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreAuthDto {

    private Long preAuthId;
    private Long paymentId;
    private PreAuthState preAuthState;
}
