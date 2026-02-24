package koz.dev.paymentservice.domain;

import koz.dev.commonlibs.http.payment.CreatePaymentRequestDto;
import koz.dev.commonlibs.http.payment.CreatePaymentResponseDto;
import koz.dev.commonlibs.http.payment.PaymentMethod;
import koz.dev.commonlibs.http.payment.PaymentStatus;
import koz.dev.paymentservice.domain.db.PaymentMapper;
import koz.dev.paymentservice.domain.db.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {
    private PaymentRepository paymentRepository;
    private final PaymentMapper mapper;

    public CreatePaymentResponseDto makePayment(CreatePaymentRequestDto request) {
        var found = paymentRepository.findByOrderId(request.orderId());
        if(found.isPresent()) {
            log.info("Payment request already exists for orderId={}",request.orderId());
            return mapper.toResponseDto(found.get());
        }
        var entity = mapper.toEntity(request);

        var status= request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCEEDED;
        entity.setPaymentStatus(status);
        var savedEntity= paymentRepository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }
}
