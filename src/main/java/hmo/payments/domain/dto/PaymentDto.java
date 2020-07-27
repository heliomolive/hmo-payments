package hmo.payments.domain.dto;

import hmo.payments.domain.enums.PaymentState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long paymentId;
    private PaymentState state;
    private BigDecimal amount;
    private Set<PreAuthDto> preAuthSet;
    private Set<AuthDto> authSet;
}
