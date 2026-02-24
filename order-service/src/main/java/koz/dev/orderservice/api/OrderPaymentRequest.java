package koz.dev.orderservice.api;

import koz.dev.commonlibs.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        Long orderId,
        PaymentMethod paymentMethod
) {
}
