package koz.dev.commonlibs.kafka;

import koz.dev.commonlibs.http.payment.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderPaidEvent(
        Long orderId,
        Long paymentId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {
}
