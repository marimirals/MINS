package ru.iu3.lab1.transportcompany.repository;

import ru.iu3.lab1.transportcompany.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
}
