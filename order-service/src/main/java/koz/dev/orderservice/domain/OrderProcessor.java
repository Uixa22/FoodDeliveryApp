package koz.dev.orderservice.domain;


import koz.dev.commonlibs.http.order.CreateOrderRequestDto;
import koz.dev.commonlibs.http.order.OrderStatus;
import koz.dev.commonlibs.http.payment.CreatePaymentRequestDto;
import koz.dev.commonlibs.http.payment.CreatePaymentResponseDto;
import koz.dev.commonlibs.http.payment.PaymentStatus;
import koz.dev.commonlibs.kafka.DeliveryAssignedEvent;
import koz.dev.commonlibs.kafka.OrderPaidEvent;
import koz.dev.orderservice.api.OrderPaymentRequest;
import koz.dev.orderservice.domain.db.OrderEntity;
import koz.dev.orderservice.domain.db.OrderEntityMapper;
import koz.dev.orderservice.domain.db.OrderItemEntity;
import koz.dev.orderservice.domain.db.OrderRepository;
import koz.dev.orderservice.external.PaymentHttpClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service

@Slf4j
public class OrderProcessor {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    public OrderEntity create (CreateOrderRequestDto request) {
        var entity = orderEntityMapper.toEntity(request);
        calculatePricingForOrder(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        return orderRepository.save(entity);
    }

    private void calculatePricingForOrder(OrderEntity entity) {
        BigDecimal totalPrice= BigDecimal.ZERO;
        for (OrderItemEntity item:entity.getItems()){
            var randomPrice= ThreadLocalRandom.current().nextDouble(100,5000);
            item.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));
            totalPrice= item.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .add(totalPrice);
        }
        entity.setTotalAmount(totalPrice);

    }

    public OrderEntity getOrderOrThrow(Long id) {
        var orderEntityOptional=orderRepository.findById(id);
        return orderEntityOptional
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id = '%s' not found"));
    }
    public OrderEntity processPayment(
            Long id,
            OrderPaymentRequest request
    ) {
        var entity= getOrderOrThrow(id);
        if(!entity.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)){
            throw new RuntimeException("Order Status must be is: PENDING_PAYMENT");
        }
        var response=paymentHttpClient.createPayment(CreatePaymentRequestDto.builder()
                .orderId(id)
                .paymentMethod(request.paymentMethod())
                .amount(entity.getTotalAmount())
                .build());
        var status=response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCEEDED)
                ?OrderStatus.PAID
                :OrderStatus.PAYMENT_FAILED;
        entity.setOrderStatus(status);

        if(status.equals(OrderStatus.PAID)){
            sendOrderPaidEvent(entity,response);
        }
        return orderRepository.save(entity);

    }

    private void sendOrderPaidEvent(
            OrderEntity entity,
            CreatePaymentResponseDto response
    ) {
        kafkaTemplate.send(
                orderPaidTopic,
                entity.getId(),
                OrderPaidEvent.builder()
                        .orderId(entity.getId())
                        .paymentId(response.paymentId())
                        .paymentMethod(response.paymentMethod())
                        .amount(entity.getTotalAmount())
                        .build()
        ).thenAccept(result->
                log.info("Order Paid event send : id{}",entity.getId()));
    }

    public void deliveryAssignedEvent(DeliveryAssignedEvent event) {
        var order=getOrderOrThrow(event.orderId());
        if(order.getOrderStatus().equals(OrderStatus.DELIVERY_ASSIGNED)){
            log.info("order delivery already process: orderId {}",event.orderId());
            return;
        }else if(!order.getOrderStatus().equals(OrderStatus.PAID)){
            log.info("incorrect state : orderId {}, status {}",order.getId(),order.getOrderStatus());
            return;
        }
        order.setOrderStatus(OrderStatus.DELIVERY_ASSIGNED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        orderRepository.save(order);
        log.info("Order delivery assigned process with id:{}",order.getId());



    }
}
