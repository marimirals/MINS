package ru.iu3.lab1.transportcompany.state;

import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;

public class DeliveredState implements OrderState {

    @Override
    public boolean canAssignVehicle() {
        return false; // Доставленный заказ нельзя менять
    }

    @Override
    public void next(Order order) {
        // Доставленный заказ нельзя изменить
        throw new IllegalStateException("Заказ уже доставлен. Нельзя изменить статус.");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Нельзя отменить доставленный заказ!");
    }

    @Override
    public String getName() {
        return "DELIVERED";
    }

}