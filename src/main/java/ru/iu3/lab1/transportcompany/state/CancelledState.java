package ru.iu3.lab1.transportcompany.state;

import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;

public class CancelledState implements OrderState {

    @Override
    public boolean canAssignVehicle() {
        return false; // Отменённый заказ — нельзя назначать транспорт
    }

    @Override
    public void next(Order order) {
        throw new IllegalStateException("Отмененный заказ нельзя изменить.");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Заказ уже отменен.");
    }

    @Override
    public String getName() {
        return "CANCELLED";
    }

}