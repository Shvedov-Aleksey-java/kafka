package aleksey.emailnotificationmicroservice.kafka_consumer.config;

import aleksey.emailnotificationmicroservice.kafka_consumer.excepton.NonRetryableException;
import aleksey.emailnotificationmicroservice.kafka_consumer.excepton.RetryableException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Configuration
public class KafkaConfig {

    /**
     * содержит все настройки из файла проперти
     */
    @Autowired
    private Environment environment;

    /**
     * создали бин конфигурации для консьюмера
     */
    @Bean
    ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.consumer.bootstrap-servers"),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"),
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                ConsumerConfig.ISOLATION_LEVEL_CONFIG, environment.getProperty("spring.kafka.consumer.isolation-level").toLowerCase()
        ));
    }


    /**
     * внедрили наш бин consumerFactory конфигурации так же внедрили бин kafkaTemplate
     */
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> listenerContainerFactory
            (ConsumerFactory<String, Object> consumerFactory, KafkaTemplate kafkaTemplate) {
        /**
         * errorHandler - определили поведение если мы получили обьект и не можем его десирилизовать
         * то тогда создаем новый топик и посылаем его туда за это отвечает DeadLetterPublishingRecoverer
         */
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate),
                /**
                 * FixedBackOff - отправлять каждые 3 секунды и зделать 3 попытки
                 */
                new FixedBackOff(3000, 3));
        /**
         * addNotRetryableExceptions(NonRetryableException.class); - добавили обработчику исключенний как быть с ошибками
         * которые вызывают постоянную отправку сообщения точнее сказали что бы он обрабатывал через наще исключенние
         * NonRetryableException
         */
        errorHandler.addNotRetryableExceptions(NonRetryableException.class);
        /**
         * аналогично addNotRetryableExceptions только обрабатывает сообщения которые не требуют повторной отправки
         */
        errorHandler.addRetryableExceptions(RetryableException.class);

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        /**
         * добавили конфиг
         */
        factory.setConsumerFactory(consumerFactory);
        /**
         * добавили обработчик ошибок
         */
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    /**
     * создали бин для внедрения выше
     */
    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * создали бин для внедрения в KafkaTemplate
     */
    @Bean
    ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.consumer.bootstrap-servers"),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        return new DefaultKafkaProducerFactory<>(config);
    }
}
