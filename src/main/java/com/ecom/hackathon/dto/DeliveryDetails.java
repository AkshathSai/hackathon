package com.ecom.hackathon.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class DeliveryDetails {
    @Id
    public String orderId;
    public String productName;
    public BigDecimal productCost;
    public int quantity;
    public String address;
    public String orderDate;
    public String expectedDate;
}
