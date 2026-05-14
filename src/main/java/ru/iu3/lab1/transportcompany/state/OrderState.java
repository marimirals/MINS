package ru.iu3.lab1.transportcompany.state;

import ru.iu3.lab1.transportcompany.model.Order;

public interface OrderState {
    void next(Order order);           // Переход к следующему состоянию
    void cancel(Order order);         // Отмена заказа
    String getName();                 // Имя состояния
    default boolean canAssignVehicle() {
        return false; // по умолчанию нельзя
    }
}