package hmo.payments.domain.mapper;

import hmo.payments.domain.dto.PreAuthDto;
import hmo.payments.repository.entity.PreAuth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE) //ignore unmapped properties on compilation report
public interface PreAuthMapper {

    @Mapping(target = "paymentId", source = "payment.paymentId")
    PreAuthDto getPreAuthDto(PreAuth auth);
}
