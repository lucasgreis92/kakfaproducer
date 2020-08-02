package br.com.lgrapplications.kafka.kafkaproducer.producer;

import br.com.lgrapplications.kafka.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Component
public class OrderProducer {

    @Value("${order.topic}")
    private String orderTopic;

    private final KafkaTemplate kafkaTemplate;

    public OrderProducer(final KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(final @RequestBody OrderDTO order) {
        final String mensageKey = UUID.randomUUID().toString();
        kafkaTemplate.send(orderTopic, mensageKey, order);
    }
}
