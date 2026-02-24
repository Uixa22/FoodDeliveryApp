package koz.dev.commonlibs.http.order;



import java.math.BigDecimal;
import java.util.Set;



public record OrderDTO(
        Long id,
        Long customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemDTO> orderItemEntities
) {
}
