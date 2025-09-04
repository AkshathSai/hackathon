package com.ecom.hackathon.service;

import com.ecom.hackathon.dto.DeliveryDetails;
import com.ecom.hackathon.dto.KafkaResponse;
import com.ecom.hackathon.dto.OrderDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, DeliveryDetails> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    void postOrderDetails_ShouldSendToKafkaAndReturnResponse() {
        // Given
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId("ORDER123");
        orderDetails.setProductName("Laptop");
        orderDetails.setProductCost(new BigDecimal("999.99"));
        orderDetails.setQuantity(2);
        orderDetails.setAddress("123 Main St, City");
        orderDetails.setOrderDate("2025-09-04");

        // When
        KafkaResponse response = kafkaProducer.postOrderDetails(orderDetails);

        // Then
        assertNotNull(response);
        assertEquals("Order details posted to Delivery Service", response.getStatus());
        assertNotNull(response.getDeliveryDetails());
        assertEquals("ORDER123", response.getDeliveryDetails().getOrderId());
        assertEquals("Laptop", response.getDeliveryDetails().getProductName());
        assertEquals(new BigDecimal("999.99"), response.getDeliveryDetails().getProductCost());
        assertEquals(2, response.getDeliveryDetails().getQuantity());
        assertEquals("123 Main St, City", response.getDeliveryDetails().getAddress());
        assertEquals("2025-09-04", response.getDeliveryDetails().getOrderDate());
        assertEquals("11-09-2025", response.getDeliveryDetails().getExpectedDate());

        // Verify Kafka message was sent
        ArgumentCaptor<DeliveryDetails> deliveryDetailsCaptor = ArgumentCaptor.forClass(DeliveryDetails.class);
        verify(kafkaTemplate).send(eq("ecom-orders"), deliveryDetailsCaptor.capture());

        DeliveryDetails sentDeliveryDetails = deliveryDetailsCaptor.getValue();
        assertEquals("ORDER123", sentDeliveryDetails.getOrderId());
        assertEquals("Laptop", sentDeliveryDetails.getProductName());
        assertEquals("11-09-2025", sentDeliveryDetails.getExpectedDate());
    }

    @Test
    void convertOrderDetailsToOrderResponse_ShouldMapAllFields() {
        // Given
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId("ORDER456");
        orderDetails.setProductName("Smartphone");
        orderDetails.setProductCost(new BigDecimal("599.50"));
        orderDetails.setQuantity(1);
        orderDetails.setAddress("456 Oak Ave");
        orderDetails.setOrderDate("2025-09-05");

        // When
        DeliveryDetails result = kafkaProducer.convertOrderDetailsToOrderResponse(orderDetails);

        // Then
        assertNotNull(result);
        assertEquals("ORDER456", result.getOrderId());
        assertEquals("Smartphone", result.getProductName());
        assertEquals(new BigDecimal("599.50"), result.getProductCost());
        assertEquals(1, result.getQuantity());
        assertEquals("456 Oak Ave", result.getAddress());
        assertEquals("2025-09-05", result.getOrderDate());
        assertNull(result.getExpectedDate()); // Should be null before setting
    }

    @Test
    void convertOrderDetailsToOrderResponse_ShouldHandleNullValues() {
        // Given
        OrderDetails orderDetails = new OrderDetails();
        // Only set some fields

        // When
        DeliveryDetails result = kafkaProducer.convertOrderDetailsToOrderResponse(orderDetails);

        // Then
        assertNotNull(result);
        assertNull(result.getOrderId());
        assertNull(result.getProductName());
        assertNull(result.getProductCost());
        assertEquals(0, result.getQuantity()); // int defaults to 0
        assertNull(result.getAddress());
        assertNull(result.getOrderDate());
        assertNull(result.getExpectedDate());
    }

    @Test
    void postOrderDetails_ShouldHandleNullOrderDetails() {
        // Given
        OrderDetails orderDetails = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> kafkaProducer.postOrderDetails(orderDetails));
    }
}
