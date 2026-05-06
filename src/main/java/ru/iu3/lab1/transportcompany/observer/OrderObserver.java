package ru.iu3.lab1.transportcompany.observer;

import ru.iu3.lab1.transportcompany.model.Order;

public interface OrderObserver {
    void update(Order order, String message);
    String getNotifierType();
}