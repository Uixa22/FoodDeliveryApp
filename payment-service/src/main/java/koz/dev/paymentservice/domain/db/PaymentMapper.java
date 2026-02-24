package koz.dev.paymentservice.domain.db;

import koz.dev.commonlibs.http.payment.CreatePaymentRequestDto;
import koz.dev.commonlibs.http.payment.CreatePaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
     PaymentEntity toEntity(CreatePaymentRequestDto request);

     @Mapping(source="id",target="paymentId")
     CreatePaymentResponseDto toResponseDto(PaymentEntity entity);
}
