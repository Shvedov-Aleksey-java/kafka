package aleksey.emailnotificationmicroservice.kafka_consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedEvent {
    private String UUID;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
