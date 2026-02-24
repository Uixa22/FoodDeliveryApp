package koz.dev.paymentservice.domain.db;

import jakarta.persistence.*;
import koz.dev.commonlibs.http.payment.PaymentMethod;
import koz.dev.commonlibs.http.payment.PaymentStatus;
import lombok.*;


import java.math.BigDecimal;

@Entity
@Table(name="payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id",nullable = false)
    private Long id;

    @Column(name="order_id")
    private Long orderId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status",nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method",nullable = false)
    private PaymentMethod paymentMethod;


}
