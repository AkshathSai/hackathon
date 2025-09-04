package com.ecom.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaResponse {
    public String status;
    public DeliveryDetails deliveryDetails;
}

