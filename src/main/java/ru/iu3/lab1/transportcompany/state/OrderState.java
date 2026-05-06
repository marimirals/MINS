package ru.iu3.lab1.transportcompany.state;

import ru.iu3.lab1.transportcompany.model.Order;

public interface OrderState {
    void next(Order order);           // Переход к следующему состоянию
    void cancel(Order order);         // Отмена заказа
    String getName();                 // Имя состояния
    boolean canCancel();              // Можно ли отменить
    boolean canProceed();             // Можно ли перейти дальше
}