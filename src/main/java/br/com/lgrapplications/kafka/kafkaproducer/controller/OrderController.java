package br.com.lgrapplications.kafka.kafkaproducer.controller;


import br.com.lgrapplications.kafka.dto.OrderDTO;
import br.com.lgrapplications.kafka.kafkaproducer.producer.OrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rs/orders")
@Slf4j
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public void send(@RequestBody OrderDTO order) {
        orderProducer.send(order);
    }
}
