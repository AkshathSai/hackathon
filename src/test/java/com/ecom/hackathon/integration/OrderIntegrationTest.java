package com.ecom.hackathon.integration;

import com.ecom.hackathon.dto.DeliveryDetails;
import com.ecom.hackathon.dto.OrderDetails;
import com.ecom.hackathon.repository.DeliveryDetailsRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, DeliveryDetails> kafkaTemplate;

    @MockitoBean
    private DeliveryDetailsRepo deliveryDetailsRepo;

    @Test
    void fullOrderFlow_ShouldProcessOrderSuccessfully() throws Exception {
        // Given
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId("INTEGRATION_TEST_ORDER");
        orderDetails.setProductName("Integration Test Product");
        orderDetails.setProductCost(new BigDecimal("123.45"));
        orderDetails.setQuantity(3);
        orderDetails.setAddress("Integration Test Address");
        orderDetails.setOrderDate("2025-09-04");

        // When & Then
        mockMvc.perform(post("/orders/v1/kafka")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("Order details posted to Delivery Service"))
                .andExpect(jsonPath("$.deliveryDetails.orderId").value("INTEGRATION_TEST_ORDER"))
                .andExpect(jsonPath("$.deliveryDetails.productName").value("Integration Test Product"))
                .andExpect(jsonPath("$.deliveryDetails.productCost").value(123.45))
                .andExpect(jsonPath("$.deliveryDetails.quantity").value(3))
                .andExpect(jsonPath("$.deliveryDetails.address").value("Integration Test Address"))
                .andExpect(jsonPath("$.deliveryDetails.orderDate").value("2025-09-04"))
                .andExpect(jsonPath("$.deliveryDetails.expectedDate").value("11-09-2025"));
    }
}
