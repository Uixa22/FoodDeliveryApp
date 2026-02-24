package koz.dev.paymentservice.api;

import koz.dev.commonlibs.http.payment.CreatePaymentRequestDto;
import koz.dev.commonlibs.http.payment.CreatePaymentResponseDto;
import koz.dev.paymentservice.domain.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Slf4j
@AllArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;

    @PostMapping
    public CreatePaymentResponseDto createPayment(
            @RequestBody CreatePaymentRequestDto request
    ) {
        log.info("Received request: paymentRequest={}",request);

        return paymentService.makePayment(request);
    }

}
