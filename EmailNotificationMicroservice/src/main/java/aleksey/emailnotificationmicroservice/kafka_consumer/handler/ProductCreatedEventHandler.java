package aleksey.emailnotificationmicroservice.kafka_consumer.handler;

import aleksey.emailnotificationmicroservice.kafka_consumer.excepton.NonRetryableException;
import aleksey.emailnotificationmicroservice.kafka_consumer.excepton.RetryableException;
import aleksey.emailnotificationmicroservice.kafka_consumer.model.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@KafkaListener(topics = "product-create-events-topic")
public class ProductCreatedEventHandler {

    private RestTemplate template;

    public ProductCreatedEventHandler(RestTemplate template) {
        this.template = template;
    }

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        log.info("Received event: {}", productCreatedEvent.getTitle());
        String url = "http://localhost:8090/response/200";
        try {
            ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                log.info("Received event: {}", response.getBody());
            }
            /**
             * ResourceAccessException - ошибка ресурс не доступен повторяем малоли микросервис поднимится
             */
        } catch (ResourceAccessException e) {
            log.error(e.getMessage());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }
}
