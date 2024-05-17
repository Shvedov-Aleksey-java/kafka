package aleksey.shvedov.productmicroservice.kafka_producer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateEvent {
    private String UUID;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
