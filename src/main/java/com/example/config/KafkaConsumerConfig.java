package com.example.config;

import com.example.model.dto.TransactionHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value(value = "${spring.kafka.dead_letter_topic}")
    private String deadLetterTopic;

    @Value("${kafka.group-id}")
    private String groupId;

    private final KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate;

    public KafkaConsumerConfig(KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


//    @Bean
//    public ConsumerFactory<String, TransactionHistoryDto> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//
//
////        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
////        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 10);
////        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        // Specify the value deserializer with the object type
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        // Specify the value class for the JsonDeserializer
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // You can restrict trusted packages for security
////        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Telegram.class);
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }


    @Bean
    public ConsumerFactory<String, TransactionHistoryDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props);
    }
//    @Bean
//    public ConsumerFactory<Object, TransactionResponse> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // You can restrict trusted packages for security
//        return new DefaultKafkaConsumerFactory<>(props);
//    }

//    @Bean
//    KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(ConsumerFactory<String, Notification> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, Notification> kafkaListenerContainerFactory
//                = new ConcurrentKafkaListenerContainerFactory<>();
//        kafkaListenerContainerFactory.setConcurrency(2);
//        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory);
//        return kafkaListenerContainerFactory;
//    }

    @Bean("kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, TransactionHistoryDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionHistoryDto> concurrentKafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate, (record, ex) -> {
                    log.info("Exception {} occurred sending the record to the error topic {}", ex.getMessage(), deadLetterTopic);
                    return new TopicPartition(deadLetterTopic, -1);
                });
        CommonErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, new FixedBackOff(10000L, 2L));
        concurrentKafkaListenerContainerFactory.setCommonErrorHandler(errorHandler);
        return concurrentKafkaListenerContainerFactory;
    }
}

