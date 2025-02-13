package aleksey.shvedov.productmicroservice.kafka_producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDto {
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
