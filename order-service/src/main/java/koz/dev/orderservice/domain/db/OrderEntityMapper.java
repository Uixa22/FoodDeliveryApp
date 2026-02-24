package koz.dev.orderservice.domain.db;

import koz.dev.commonlibs.http.order.CreateOrderRequestDto;
import koz.dev.commonlibs.http.order.OrderDTO;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OrderEntityMapper {
    OrderEntity toEntity(CreateOrderRequestDto requestDto);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity
                .getItems()
                .forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }
    OrderDTO toOrderDTO(OrderEntity orderEntity);
}
