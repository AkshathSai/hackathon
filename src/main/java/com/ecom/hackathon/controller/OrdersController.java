package com.ecom.hackathon.controller;

import com.ecom.hackathon.dto.OrderDetails;
import com.ecom.hackathon.dto.KafkaResponse;
import com.ecom.hackathon.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrdersController {

    final KafkaProducer kafkaProducer;

    @PostMapping("/orders/v1/kafka")
    public KafkaResponse postToKafka(@RequestBody OrderDetails orderDetails) {
        return kafkaProducer.postOrderDetails(orderDetails);
    }
}
