package ru.iu3.lab1.transportcompany.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private String from;
    private String to;
    private double weight;
    private OrderStatus status;
    private String vehicleId;
    private double price;
}
