package koz.dev.deliveryservice.kafka;

import koz.dev.commonlibs.kafka.OrderPaidEvent;
import koz.dev.deliveryservice.DeliveryProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;


@EnableKafka
@Configuration
@Slf4j
@AllArgsConstructor
public class OrderPaidKafkaConsumer {

    private final DeliveryProcessor deliveryProcessor;
    @KafkaListener(
            topics="${order-paid-topic}",
            containerFactory = "orderPaidEventListenerFactory"
    )
    public void listen(OrderPaidEvent event) {
        log.info("Received OrderPaidEvent {}", event);
        deliveryProcessor.orderPaidEvent(event);
    }
}
