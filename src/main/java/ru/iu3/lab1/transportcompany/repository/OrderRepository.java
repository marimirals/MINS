package ru.iu3.lab1.transportcompany.repository;

import ru.iu3.lab1.transportcompany.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();

    /* IIIII - interface segregation (не дело репозитория -> нарушение)
    void sendEmail(Order order);
    void generatePDF(Order order);
    void deleteAll();
    List<Order> findByStatus(OrderStatus status);
    void exportToExcel(String filename);
     */

}
