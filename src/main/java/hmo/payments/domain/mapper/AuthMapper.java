package hmo.payments.domain.mapper;

import hmo.payments.domain.dto.AuthDto;
import hmo.payments.repository.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE) //ignore unmapped properties on compilation report
public interface AuthMapper {

    @Mapping(target = "paymentId", source = "payment.paymentId")
    AuthDto getAuthDto(Auth auth);
}
