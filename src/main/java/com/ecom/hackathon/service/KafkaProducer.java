package com.ecom.hackathon.service;

import com.ecom.hackathon.dto.KafkaResponse;
import com.ecom.hackathon.dto.OrderDetails;
import com.ecom.hackathon.dto.DeliveryDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    final String ECOM_ORDER_TOPIC = "ecom-orders";
    final KafkaTemplate<String, DeliveryDetails> kafkaTemplate;

    public KafkaResponse postOrderDetails(OrderDetails orderDetails) {
        DeliveryDetails deliveryDetails = generateDeliveryDetails(orderDetails);
        // Assuming expected delivery is 7 days from order date for simplicity
        deliveryDetails.setExpectedDate("11-09-2025"); // Placeholder date
        kafkaTemplate.send(ECOM_ORDER_TOPIC, deliveryDetails);
        log.info("Posted Delivery Details: {}", deliveryDetails);
        return new KafkaResponse("Order details posted to Delivery Service", deliveryDetails);
    }

    public DeliveryDetails generateDeliveryDetails(OrderDetails orderDetails) {
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setOrderId(orderDetails.getOrderId());
        deliveryDetails.setProductName(orderDetails.getProductName());
        deliveryDetails.setProductCost(orderDetails.getProductCost());
        deliveryDetails.setQuantity(orderDetails.getQuantity());
        deliveryDetails.setAddress(orderDetails.getAddress());
        deliveryDetails.setOrderDate(orderDetails.getOrderDate());
        return deliveryDetails;
    }
}
