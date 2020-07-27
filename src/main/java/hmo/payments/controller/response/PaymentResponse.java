package hmo.payments.controller.response;

import hmo.payments.domain.dto.AuthDto;
import hmo.payments.domain.dto.PreAuthDto;
import hmo.payments.domain.enums.PaymentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private BigDecimal amount;
    private PaymentState state;
    private Set<PreAuthDto> preAuthSet;
    private Set<AuthDto> authSet;
}
