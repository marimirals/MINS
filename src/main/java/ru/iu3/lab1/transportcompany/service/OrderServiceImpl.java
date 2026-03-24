package ru.iu3.lab1.transportcompany.service;

import org.springframework.stereotype.Service;
import ru.iu3.lab1.transportcompany.exception.OrderNotFoundException;
import ru.iu3.lab1.transportcompany.exception.VehicleNotAvailableException;
import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;
import ru.iu3.lab1.transportcompany.model.Vehicle;
import ru.iu3.lab1.transportcompany.pricing.PricingStrategy;
import ru.iu3.lab1.transportcompany.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PricingStrategy pricingStrategy;
    private final VehicleService vehicleService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            PricingStrategy pricingStrategy,
                            VehicleService vehicleService) {
        this.orderRepository = orderRepository;
        this.pricingStrategy = pricingStrategy;
        this.vehicleService = vehicleService;
    }

    @Override
    public Order createOrder(String from, String to, double weight) {
        Order order = new Order(UUID.randomUUID().toString(), from, to, weight, OrderStatus.CREATED, null);
        orderRepository.save(order);
        return order;
    }

    @Override
    public void assignVehicle(String orderId, String vehicleId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // транспорт существует и подходит по весу
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle.getCapacity() < order.getWeight()) {
            throw new VehicleNotAvailableException(
                    "Vehicle " + vehicleId + " capacity (" + vehicle.getCapacity() +
                            ") is less than order weight (" + order.getWeight() + ")");
        }

        order.setVehicleId(vehicleId);
        order.setStatus(OrderStatus.ASSIGNED);

        orderRepository.save(order);
    }

    @Override
    public void updateStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public double calculatePrice(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return pricingStrategy.calculate(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
