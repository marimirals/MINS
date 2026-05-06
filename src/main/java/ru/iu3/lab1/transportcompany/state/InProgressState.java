package ru.iu3.lab1.transportcompany.state;

import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;

public class InProgressState implements OrderState {

    @Override
    public boolean canAssignVehicle() {
        return false; // Транспорт уже назначен
    }

    @Override
    public void next(Order order) {
        // В пути -> Доставлен
        order.setStatus(OrderStatus.DELIVERED);
        order.setState(new DeliveredState());
        order.notifyObservers("Ваш заказ доставлен! Спасибо за выбор нашей компании.");
        System.out.println("Заказ доставлен!");
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        order.setState(new CancelledState());
        order.notifyObservers("Заказ отменен во время доставки. Свяжитесь с поддержкой.");
        System.out.println("Заказ отменен");
    }

    @Override
    public String getName() {
        return "IN_PROGRESS";
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public boolean canProceed() {
        return true; // Можно доставить
    }
}