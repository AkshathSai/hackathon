package com.ecom.hackathon.controller;

import com.ecom.hackathon.dto.DeliveryDetails;
import com.ecom.hackathon.dto.KafkaResponse;
import com.ecom.hackathon.dto.OrderDetails;
import com.ecom.hackathon.service.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaProducer kafkaProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void postToKafka_ShouldReturnKafkaResponse_WhenValidOrderDetailsProvided() throws Exception {
        // Given
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId("ORDER123");
        orderDetails.setProductName("Laptop");
        orderDetails.setProductCost(new BigDecimal("999.99"));
        orderDetails.setQuantity(1);
        orderDetails.setAddress("123 Main St");
        orderDetails.setOrderDate("2025-09-04");

        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId("ORDER123");
        deliveryDetails.setProductName("Laptop");
        deliveryDetails.setProductCost(new BigDecimal("999.99"));
        deliveryDetails.setQuantity(1);
        deliveryDetails.setAddress("123 Main St");
        deliveryDetails.setOrderDate("2025-09-04");
        deliveryDetails.setExpectedDate("2025-09-11");

        KafkaResponse expectedResponse = new KafkaResponse("Order details posted to Delivery Service", deliveryDetails);

        when(kafkaProducer.postOrderDetails(any(OrderDetails.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/orders/v1/kafka")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("Order details posted to Delivery Service"))
                .andExpect(jsonPath("$.deliveryDetails.orderId").value("ORDER123"))
                .andExpect(jsonPath("$.deliveryDetails.productName").value("Laptop"))
                .andExpect(jsonPath("$.deliveryDetails.productCost").value(999.99))
                .andExpect(jsonPath("$.deliveryDetails.quantity").value(1))
                .andExpect(jsonPath("$.deliveryDetails.address").value("123 Main St"));
    }

    @Test
    void postToKafka_ShouldHandleEmptyOrderDetails() throws Exception {
        // Given
        OrderDetails emptyOrderDetails = new OrderDetails();
        KafkaResponse response = new KafkaResponse("Order details posted to Delivery Service", new DeliveryDetails());

        when(kafkaProducer.postOrderDetails(any(OrderDetails.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/orders/v1/kafka")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyOrderDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Order details posted to Delivery Service"));
    }
}
