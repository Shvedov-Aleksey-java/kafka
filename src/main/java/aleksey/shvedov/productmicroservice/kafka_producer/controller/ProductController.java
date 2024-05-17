package aleksey.shvedov.productmicroservice.kafka_producer.controller;

import aleksey.shvedov.productmicroservice.kafka_producer.dto.CreateProductDto;
import aleksey.shvedov.productmicroservice.kafka_producer.servise.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * зделалаи два контролера для отправки сообщений на кафка сервер
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    /**
     * отправка сообщений в аснихронном режиме
     */
    @PostMapping("/async")
    public ResponseEntity<String> createProductAsync(@RequestBody CreateProductDto productDto) {
        final String productUUID = service.createProductAsync(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productUUID);
    }

    /**
     * отправка сообщений в синхронном режиме
     */
    @PostMapping("/sync")
    public ResponseEntity<String> createProductSync(@RequestBody CreateProductDto productDto) {
        final String productUUID = service.createProductSync(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productUUID);
    }
}
