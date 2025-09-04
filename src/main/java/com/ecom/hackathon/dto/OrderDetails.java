package com.ecom.hackathon.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetails {
    private String orderId;
    private String productName;
    private BigDecimal productCost;
    private int quantity;
    private String address;
    private String orderDate;
}
