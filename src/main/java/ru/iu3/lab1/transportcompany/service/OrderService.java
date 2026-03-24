package ru.iu3.lab1.transportcompany.service;

import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;

import java.util.List;

public interface OrderService {
    Order createOrder(String from, String to, double weight);
    void assignVehicle(String orderId, String vehicleId);
    void updateStatus(String orderId, OrderStatus status);
    double calculatePrice(String orderId);
    List<Order> getAllOrders();
}
