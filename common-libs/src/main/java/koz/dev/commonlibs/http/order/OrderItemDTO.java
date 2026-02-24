package koz.dev.commonlibs.http.order;




public record OrderItemDTO(
        Long id,
        Long itemId,
        Integer quantity,
        Integer priceAtPurchase
) {
}