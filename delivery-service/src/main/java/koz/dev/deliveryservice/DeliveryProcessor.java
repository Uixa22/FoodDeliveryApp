package koz.dev.deliveryservice;

import koz.dev.commonlibs.kafka.DeliveryAssignedEvent;
import koz.dev.commonlibs.kafka.OrderPaidEvent;
import koz.dev.deliveryservice.domain.DeliveryEntity;
import koz.dev.deliveryservice.domain.DeliveryEntityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;


@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
@Service
public class DeliveryProcessor {
    private final DeliveryEntityRepository deliveryEntityRepository;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    @Value("${delivery-assigned-topic}")
    private String deliveryAssignedTopic;

    public void orderPaidEvent(OrderPaidEvent event) {
        var orderId=event.orderId();
        var found= deliveryEntityRepository.findByOrderId(orderId);
        if (found.isPresent()) {
            log.info("Found order delivery was already assigned : {}", found.get());
        }
        var assign=assignDelivery(orderId);
        sendDeliveryAssignEvent(assign);
    }

    private void sendDeliveryAssignEvent(DeliveryEntity assign) {
        kafkaTemplate.send(
               deliveryAssignedTopic,
                assign.getOrderId(),
                DeliveryAssignedEvent.builder()
                        .courierName(assign.getCourierName())
                        .etaMinutes(assign.getEtaMinutes())
                        .orderId(assign.getOrderId())
                        .build()
        ).thenAccept(result->
                log.info("delivery assignes with event sent: deliveryId {} ",assign.getOrderId()));
    }

    private DeliveryEntity assignDelivery(Long orderId) {
        var entity= new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierName("courier-"+ ThreadLocalRandom.current().nextInt(100));
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10,90));

        return deliveryEntityRepository.save(entity);
    }
    
}
