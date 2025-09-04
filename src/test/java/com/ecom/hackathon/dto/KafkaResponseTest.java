package com.ecom.hackathon.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class KafkaResponseTest {

    @Test
    void testKafkaResponseConstructorAndGetters() {
        // Given
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId("ORDER123");
        deliveryDetails.setProductName("Laptop");
        deliveryDetails.setProductCost(new BigDecimal("999.99"));

        // When
        KafkaResponse kafkaResponse = new KafkaResponse("Success", deliveryDetails);

        // Then
        assertEquals("Success", kafkaResponse.getStatus());
        assertEquals(deliveryDetails, kafkaResponse.getDeliveryDetails());
        assertEquals("ORDER123", kafkaResponse.getDeliveryDetails().getOrderId());
    }

    @Test
    void testKafkaResponseSetters() {
        // Given
        KafkaResponse kafkaResponse = new KafkaResponse("Initial", null);
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId("ORDER456");

        // When
        kafkaResponse.setStatus("Updated Status");
        kafkaResponse.setDeliveryDetails(deliveryDetails);

        // Then
        assertEquals("Updated Status", kafkaResponse.getStatus());
        assertEquals(deliveryDetails, kafkaResponse.getDeliveryDetails());
        assertEquals("ORDER456", kafkaResponse.getDeliveryDetails().getOrderId());
    }

    @Test
    void testKafkaResponseEquals() {
        // Given
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId("ORDER123");

        KafkaResponse response1 = new KafkaResponse("Success", deliveryDetails);
        KafkaResponse response2 = new KafkaResponse("Success", deliveryDetails);

        // Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testKafkaResponseToString() {
        // Given
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId("ORDER123");
        KafkaResponse kafkaResponse = new KafkaResponse("Test Status", deliveryDetails);

        // When
        String toString = kafkaResponse.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("KafkaResponse"));
        assertTrue(toString.contains("Test Status"));
    }

    @Test
    void testKafkaResponseWithNullDeliveryDetails() {
        // Given & When
        KafkaResponse kafkaResponse = new KafkaResponse("Success", null);

        // Then
        assertEquals("Success", kafkaResponse.getStatus());
        assertNull(kafkaResponse.getDeliveryDetails());
    }
}
