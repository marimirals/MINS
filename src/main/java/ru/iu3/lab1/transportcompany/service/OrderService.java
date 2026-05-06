package ru.iu3.lab1.transportcompany.service;

import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;
import ru.iu3.lab1.transportcompany.pricing.PricingStrategy;

import java.util.List;

public interface OrderService {
    Order createOrder(String from, String to, double weight);
    void assignVehicle(String orderId, String vehicleId);
    void updateStatus(String orderId, OrderStatus status);
    double calculatePrice(String orderId);
    void cancelOrder(String orderId);
    void setPricingStrategy(PricingStrategy strategy);
    List<Order> getAllOrders();
}
