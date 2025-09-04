package com.ecom.hackathon.service;

import com.ecom.hackathon.dto.DeliveryDetails;
import com.ecom.hackathon.repository.DeliveryDetailsRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private DeliveryDetailsRepo deliveryDetailsRepo;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    void consume_ShouldSaveDeliveryDetailsToRepository() {
        // Given
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId("ORDER123");
        deliveryDetails.setProductName("Laptop");
        deliveryDetails.setProductCost(new BigDecimal("999.99"));
        deliveryDetails.setQuantity(1);
        deliveryDetails.setAddress("123 Main St");
        deliveryDetails.setOrderDate("2025-09-04");
        deliveryDetails.setExpectedDate("2025-09-11");

        when(deliveryDetailsRepo.save(any(DeliveryDetails.class))).thenReturn(deliveryDetails);

        // When
        kafkaConsumer.consume(deliveryDetails);

        // Then
        verify(deliveryDetailsRepo).save(deliveryDetails);
    }

    @Test
    void consume_ShouldHandleEmptyDeliveryDetails() {
        // Given
        DeliveryDetails deliveryDetails = new DeliveryDetails();

        when(deliveryDetailsRepo.save(any(DeliveryDetails.class))).thenReturn(deliveryDetails);

        // When
        kafkaConsumer.consume(deliveryDetails);

        // Then
        verify(deliveryDetailsRepo).save(deliveryDetails);
    }
}
