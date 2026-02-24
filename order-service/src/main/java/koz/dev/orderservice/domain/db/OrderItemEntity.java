package koz.dev.orderservice.domain.db;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name="order_item")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(name = "itemId")
    private Long itemId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name="item_name")
    private String name;

    @Column(name = "priceAtPurchase")
    private BigDecimal priceAtPurchase;


}
