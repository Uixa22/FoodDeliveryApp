package koz.dev.deliveryservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery-orders")
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "corder_id", nullable = false)
    private Long orderId;

    @Column(name = "courier_name",nullable = false)
    private String courierName;

    @Column(name = "eta_minutes",nullable = false)
    private Integer etaMinutes;





}


