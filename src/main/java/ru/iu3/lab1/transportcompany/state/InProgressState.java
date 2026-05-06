package ru.iu3.lab1.transportcompany.state;

import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;

public class InProgressState implements OrderState {

    @Override
    public void next(Order order) {
        // В пути -> Доставлен
        order.setStatus(OrderStatus.DELIVERED);
        order.setState(new DeliveredState());
        System.out.println("Заказ доставлен!");
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        order.setState(new CancelledState());
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