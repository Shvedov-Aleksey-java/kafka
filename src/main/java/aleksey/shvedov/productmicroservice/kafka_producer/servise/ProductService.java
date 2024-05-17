package aleksey.shvedov.productmicroservice.kafka_producer.servise;

import aleksey.shvedov.productmicroservice.kafka_producer.dto.CreateProductDto;

public interface ProductService {
    String createProductAsync(CreateProductDto productDto);

    String createProductSync(CreateProductDto productDto);


}
