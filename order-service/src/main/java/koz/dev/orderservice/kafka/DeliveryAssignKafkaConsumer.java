package koz.dev.orderservice.kafka;

import koz.dev.commonlibs.kafka.DeliveryAssignedEvent;
import koz.dev.orderservice.domain.OrderProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;


@EnableKafka
@Configuration
@Slf4j
@AllArgsConstructor
public class DeliveryAssignKafkaConsumer {

    private final OrderProcessor orderProcessor;
    @KafkaListener(
            topics="${delivery-assigned-topic}",
            containerFactory = "deliveryAssignedEventEventListenerFactory"
    )
    public void listen(DeliveryAssignedEvent event) {
        log.info("Received Delivery Assigned Event {}", event);
        orderProcessor.deliveryAssignedEvent(event);
    }
}
