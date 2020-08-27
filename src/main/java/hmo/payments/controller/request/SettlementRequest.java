package hmo.payments.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRequest {

    @NotNull
    @AssertTrue(message = "Attribute settled must be true")
    private Boolean settled;
}
