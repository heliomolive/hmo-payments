package hmo.payments.domain.mapper;

import hmo.payments.controller.response.PaymentResponse;
import hmo.payments.domain.dto.PaymentDto;
import hmo.payments.repository.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE) //ignore unmapped properties on compilation report
public interface PaymentMapper {

    PaymentDto getPaymentDto(Payment payment);

    PaymentResponse getPaymentResponse(PaymentDto paymentDto);
}
