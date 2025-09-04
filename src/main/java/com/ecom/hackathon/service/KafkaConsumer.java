package com.ecom.hackathon.service;

import com.ecom.hackathon.dto.DeliveryDetails;
import com.ecom.hackathon.repository.DeliveryDetailsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    final DeliveryDetailsRepo deliveryDetailsRepo;

    @KafkaListener(topics = "ecom-orders", groupId = "ecom-group")
    public void consume(@Payload DeliveryDetails deliveryDetails) {
        log.info("Received Order Details: {}", deliveryDetails);
        deliveryDetailsRepo.save(deliveryDetails);
    }
}
