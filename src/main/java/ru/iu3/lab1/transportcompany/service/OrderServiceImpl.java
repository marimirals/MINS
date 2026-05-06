package ru.iu3.lab1.transportcompany.service;

import org.springframework.stereotype.Service;
import ru.iu3.lab1.transportcompany.exception.InvalidWeightException;
import ru.iu3.lab1.transportcompany.exception.OrderNotFoundException;
import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;
import ru.iu3.lab1.transportcompany.pricing.PricingContext;
import ru.iu3.lab1.transportcompany.pricing.PricingStrategy;
import ru.iu3.lab1.transportcompany.pricing.WeightBasedPricingStrategy;
import ru.iu3.lab1.transportcompany.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    /* DDDDD - dependency inversion (конкретный CsvOrderRepository вместо интерфейса, зависит
    private final CsvOrderRepository orderRepository;
     */
    private final OrderRepository orderRepository;
    private PricingStrategy currentStrategy;
    private final VehicleService vehicleService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            WeightBasedPricingStrategy defaultStrategy,
                            VehicleService vehicleService) {
        this.orderRepository = orderRepository;
        this.currentStrategy = defaultStrategy;
        this.vehicleService = vehicleService;
    }

    @Override
    public Order createOrder(String from, String to, double weight) {

        if (weight <= 0 || weight > 1000) {
            throw new InvalidWeightException(weight);
        }

        Order order = new Order(UUID.randomUUID().toString(), from, to, weight, OrderStatus.NEW, null, 0);

        order.setPrice(currentStrategy.calculate(order));

        orderRepository.save(order);

        /* SSSSS - single responsibility (создание заказа твечает за запись в файл)
        try {
        Path file = Paths.get("orders.csv");
        String line = String.format(Locale.US, "%s,%s,%s,%.2f,%s,%s",
                order.getId(), order.getFrom(), order.getTo(),
                order.getWeight(), order.getStatus().name(),
                order.getVehicleId() != null ? order.getVehicleId() : "");
        Files.write(file, Collections.singletonList(line),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
        throw new RuntimeException("Ошибка записи", e);
    }

         */

        return order;
    }

    @Override
    public void assignVehicle(String orderId, String vehicleId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        vehicleService.getVehicleById(vehicleId);

        order.setVehicleId(vehicleId);
        order.setStatus(OrderStatus.IN_PROGRESS);
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

        return order.getPrice();

        /* OOOOO (open-closed, хардкод в сервисе)
        if (order.getWeight() > 500) {
            return order.getWeight() * 15;
        } else if (order.getWeight() > 100) {
            return order.getWeight() * 12;
        } else {
            return order.getWeight() * 10;
        }*/
    }

    public void setPricingStrategy(PricingStrategy strategy) {
        this.currentStrategy = strategy;
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Нельзя отменить доставленный заказ");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
