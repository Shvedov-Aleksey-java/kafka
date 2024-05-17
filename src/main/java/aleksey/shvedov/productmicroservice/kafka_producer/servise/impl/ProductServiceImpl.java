package aleksey.shvedov.productmicroservice.kafka_producer.servise.impl;

import aleksey.shvedov.productmicroservice.kafka_producer.dto.CreateProductDto;
import aleksey.shvedov.productmicroservice.kafka_producer.mapper.ProductMapper;
import aleksey.shvedov.productmicroservice.kafka_producer.model.ProductCreateEvent;
import aleksey.shvedov.productmicroservice.kafka_producer.servise.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.TransformerException;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * @Transactional(
 *         value = "kafkaTransactionManager",
 *         rollbackFor = {ConnectException.class, TransformerException.class},
 *         noRollbackFor = {RuntimeException.class, HttpRetryException.class}
 * )
 * value = имя бина который мы внедрили для контроля транзакцый
 * rollbackFor = классы ошибок после которых пойдет откат транзакций
 * noRollbackFor = классы ощибок откат не требуется
 * по умолчанию вносить не чего не нужно спринговая анатацыя зделает все сама
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    /**
     * спринговый враппер для отправки запросов на сервер кафка
     */
    private final KafkaTemplate<String, ProductCreateEvent> kafkaTemplate;
    @Value("${topic.name}")
    private String topicName;

    /**
     * работа в асиннхронном режиме не дожидаясь ответа
     */
    @Override
    public String createProductAsync(CreateProductDto productDto) {
        // TODO save db
        String productUUID = UUID.randomUUID().toString();
        ProductCreateEvent event = ProductMapper.createEvent(productDto, productUUID);
        /**
         * CompletableFuture<SendResult<Object, Object>> : ответ от кафка брокера
         */
        CompletableFuture<SendResult<String, ProductCreateEvent>> future = kafkaTemplate
                .send(topicName, productUUID, event);
        /**
         * whenComplete метод который ожидает какой то ответ
         */
        future.whenComplete((result, error) -> {
            if (error != null) {
                log.error("Failed to send message: {}", error.getMessage());
            } else {
                log.info("Message successfully: {}", result.getRecordMetadata());
            }
        });
        /**
         * метод джоин делает так что бы сначала прищол ответ а потом уже продолжает работу программы
         */
        //future.join();
        log.info("return: {}", productUUID);
        return productUUID;
    }

    /**
     * работа в синхронном режиме
     */
    @Override
    public String createProductSync(CreateProductDto productDto) {
        String productUUID = UUID.randomUUID().toString();

        ProductCreateEvent event = ProductMapper.createEvent(productDto, productUUID);

        SendResult<String, ProductCreateEvent> result;
        /**
         * дожидаемся и обробатываем ответ
         */
        try {
            result = kafkaTemplate.send(topicName, productUUID, event).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


        log.info("Partition: {}", result.getRecordMetadata().partition());
        log.info("Topic: {}", result.getRecordMetadata().topic());
        log.info("Offset: {}", result.getRecordMetadata().offset());

        log.info("Return: {}", productUUID);
        return productUUID;

    }
}
