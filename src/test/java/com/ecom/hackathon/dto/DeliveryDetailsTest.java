package com.ecom.hackathon.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryDetailsTest {

    @Test
    void testDeliveryDetailsGettersAndSetters() {
        // Given
        DeliveryDetails deliveryDetails = new DeliveryDetails();

        // When
        deliveryDetails.setOrderId("ORDER123");
        deliveryDetails.setProductName("Smartphone");
        deliveryDetails.setProductCost(new BigDecimal("599.50"));
        deliveryDetails.setQuantity(1);
        deliveryDetails.setAddress("456 Oak Ave");
        deliveryDetails.setOrderDate("2025-09-04");
        deliveryDetails.setExpectedDate("2025-09-11");

        // Then
        assertEquals("ORDER123", deliveryDetails.getOrderId());
        assertEquals("Smartphone", deliveryDetails.getProductName());
        assertEquals(new BigDecimal("599.50"), deliveryDetails.getProductCost());
        assertEquals(1, deliveryDetails.getQuantity());
        assertEquals("456 Oak Ave", deliveryDetails.getAddress());
        assertEquals("2025-09-04", deliveryDetails.getOrderDate());
        assertEquals("2025-09-11", deliveryDetails.getExpectedDate());
    }

    @Test
    void testDeliveryDetailsEquals() {
        // Given
        DeliveryDetails deliveryDetails1 = new DeliveryDetails();
        deliveryDetails1.setOrderId("ORDER123");
        deliveryDetails1.setProductName("Smartphone");
        deliveryDetails1.setExpectedDate("2025-09-11");

        DeliveryDetails deliveryDetails2 = new DeliveryDetails();
        deliveryDetails2.setOrderId("ORDER123");
        deliveryDetails2.setProductName("Smartphone");
        deliveryDetails2.setExpectedDate("2025-09-11");

        // Then
        assertEquals(deliveryDetails1, deliveryDetails2);
        assertEquals(deliveryDetails1.hashCode(), deliveryDetails2.hashCode());
    }

    @Test
    void testDeliveryDetailsToString() {
        // Given
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId("ORDER123");
        deliveryDetails.setExpectedDate("2025-09-11");

        // When
        String toString = deliveryDetails.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("DeliveryDetails"));
        assertTrue(toString.contains("ORDER123"));
        assertTrue(toString.contains("2025-09-11"));
    }
}
