package aleksey.shvedov.productmicroservice.kafka_producer.mapper;

import aleksey.shvedov.productmicroservice.kafka_producer.dto.CreateProductDto;
import aleksey.shvedov.productmicroservice.kafka_producer.model.ProductCreateEvent;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public static ProductCreateEvent createEvent(CreateProductDto dto, String uuid) {
        return ProductCreateEvent.builder()
                .UUID(uuid)
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .title(dto.getTitle())
                .build();
    }
}
