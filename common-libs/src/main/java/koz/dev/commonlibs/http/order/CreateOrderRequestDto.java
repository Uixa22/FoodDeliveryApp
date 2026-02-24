package koz.dev.commonlibs.http.order;

import java.util.List;

public record CreateOrderRequestDto(
        Long customerId,
        String address,
        List<OrderItemRequestDto> items
) {
}
