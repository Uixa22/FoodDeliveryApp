package koz.dev.orderservice.api;

import koz.dev.commonlibs.http.order.CreateOrderRequestDto;
import koz.dev.commonlibs.http.order.OrderDTO;
import koz.dev.orderservice.domain.OrderProcessor;
import koz.dev.orderservice.domain.db.OrderEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    public final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderEntityMapper;

    @PostMapping
    public OrderDTO create(@RequestBody CreateOrderRequestDto request) {
        log.info("Order created : request {}", request);
        var saved = orderProcessor.create(request);
        return orderEntityMapper.toOrderDTO(saved);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        log.info("Order getting: {}", id);
        var saved = orderProcessor.getOrderOrThrow(id);
        return orderEntityMapper.toOrderDTO(saved);

    }
    @PostMapping("/{id}/pay")
    public OrderDTO orderPay(
            @PathVariable Long id,
            @RequestBody OrderPaymentRequest request) {
        log.info("Pay order with id {} and request {}",id , request);
        var entity = orderProcessor.processPayment(id, request);
        return orderEntityMapper.toOrderDTO(entity);
    }


}
