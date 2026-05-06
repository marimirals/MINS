package ru.iu3.lab1.transportcompany.service;

import org.springframework.stereotype.Service;
import ru.iu3.lab1.transportcompany.exception.InvalidWeightException;
import ru.iu3.lab1.transportcompany.exception.OrderNotFoundException;
import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;
import ru.iu3.lab1.transportcompany.observer.EmailNotifier;
import ru.iu3.lab1.transportcompany.observer.SmsNotifier;
import ru.iu3.lab1.transportcompany.pricing.PricingStrategy;
import ru.iu3.lab1.transportcompany.pricing.WeightBasedPricingStrategy;
import ru.iu3.lab1.transportcompany.repository.OrderRepository;
import ru.iu3.lab1.transportcompany.state.CancelledState;
import ru.iu3.lab1.transportcompany.state.DeliveredState;
import ru.iu3.lab1.transportcompany.state.InProgressState;
import ru.iu3.lab1.transportcompany.state.NewOrderState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    // DDDDD - dependency inversion - зависит от абстракций а не от конкретных реализаций.
    // (конкретный CsvOrderRepository вместо интерфейса, зависит
    /*
    private final CsvOrderRepository orderRepository;
     */
    private final OrderRepository orderRepository;
    private PricingStrategy currentStrategy;
    private final VehicleService vehicleService;
    private final EmailNotifier emailNotifier;
    private final SmsNotifier smsNotifier;

    public OrderServiceImpl(OrderRepository orderRepository,
                            WeightBasedPricingStrategy defaultStrategy,
                            VehicleService vehicleService,
                            EmailNotifier emailNotifier,
                            SmsNotifier smsNotifier) {
        this.orderRepository = orderRepository;
        this.currentStrategy = defaultStrategy;
        this.vehicleService = vehicleService;
        this.emailNotifier = emailNotifier;
        this.smsNotifier = smsNotifier;
    }

    @Override
    public Order createOrder(String from, String to, double weight) {

        if (weight <= 0 || weight > 1000) {
            throw new InvalidWeightException(weight);
        }

        Order order = new Order(UUID.randomUUID().toString(), from, to, weight, OrderStatus.NEW, null, 0, new NewOrderState(), new ArrayList<>());

        order.attachObserver(emailNotifier);
        order.attachObserver(smsNotifier);

        order.setPrice(currentStrategy.calculate(order));

        orderRepository.save(order);

        // SSSSS - single responsibility (создание заказа отвечает за запись в файл)
        /*
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

        // Проверяем существование транспорта
        vehicleService.getVehicleById(vehicleId);

        // Проверяем через состояние, можно ли назначать транспорт
        if (!order.getState().canAssignVehicle()) {
            throw new IllegalStateException(
                    "Нельзя назначить транспорт на заказ в состоянии: " + order.getState().getName()
            );
        }

        // Назначаем транспорт и переходим в следующее состояние
        order.setVehicleId(vehicleId);
        order.getState().next(order);

        orderRepository.save(order);
    }


    @Override
    public void updateStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Используем паттерн State
        if (order.getState() == null) {
            initializeState(order);
        }

        // Определяем, какое действие выполнить
        if (status == OrderStatus.CANCELLED) {
            order.getState().cancel(order);
        } else if (status == OrderStatus.IN_PROGRESS && order.getStatus() == OrderStatus.NEW) {
            order.getState().next(order);
        } else if (status == OrderStatus.DELIVERED && order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.getState().next(order);
        } else if (status != order.getStatus()) {
            throw new IllegalStateException("Недопустимый переход из " + order.getStatus() + " в " + status);
        }

        orderRepository.save(order);
    }

    @Override
    public double calculatePrice(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return order.getPrice();
    }

    public void setPricingStrategy(PricingStrategy strategy) {
        this.currentStrategy = strategy;
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.getState().cancel(order);
        orderRepository.save(order);
    }

    private void initializeState(Order order) {
        if (order.getStatus() == null) {
            order.setState(new NewOrderState());
        } else {
            switch (order.getStatus()) {
                case NEW -> order.setState(new NewOrderState());
                case IN_PROGRESS -> order.setState(new InProgressState());
                case DELIVERED -> order.setState(new DeliveredState());
                case CANCELLED -> order.setState(new CancelledState());
            }
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
