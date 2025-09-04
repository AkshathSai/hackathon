package com.ecom.hackathon.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderDetailsTest {

    @Test
    void testOrderDetailsGettersAndSetters() {
        // Given
        OrderDetails orderDetails = new OrderDetails();

        // When
        orderDetails.setOrderId("ORDER123");
        orderDetails.setProductName("Laptop");
        orderDetails.setProductCost(new BigDecimal("999.99"));
        orderDetails.setQuantity(2);
        orderDetails.setAddress("123 Main St");
        orderDetails.setOrderDate("2025-09-04");

        // Then
        assertEquals("ORDER123", orderDetails.getOrderId());
        assertEquals("Laptop", orderDetails.getProductName());
        assertEquals(new BigDecimal("999.99"), orderDetails.getProductCost());
        assertEquals(2, orderDetails.getQuantity());
        assertEquals("123 Main St", orderDetails.getAddress());
        assertEquals("2025-09-04", orderDetails.getOrderDate());
    }

    @Test
    void testOrderDetailsEquals() {
        // Given
        OrderDetails orderDetails1 = new OrderDetails();
        orderDetails1.setOrderId("ORDER123");
        orderDetails1.setProductName("Laptop");
        orderDetails1.setProductCost(new BigDecimal("999.99"));

        OrderDetails orderDetails2 = new OrderDetails();
        orderDetails2.setOrderId("ORDER123");
        orderDetails2.setProductName("Laptop");
        orderDetails2.setProductCost(new BigDecimal("999.99"));

        // Then
        assertEquals(orderDetails1, orderDetails2);
        assertEquals(orderDetails1.hashCode(), orderDetails2.hashCode());
    }

    @Test
    void testOrderDetailsToString() {
        // Given
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId("ORDER123");
        orderDetails.setProductName("Laptop");

        // When
        String toString = orderDetails.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("OrderDetails"));
        assertTrue(toString.contains("ORDER123"));
        assertTrue(toString.contains("Laptop"));
    }
}
