package ru.iu3.lab1.transportcompany.observer;

import ru.iu3.lab1.transportcompany.model.Order;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier implements OrderObserver {

    @Override
    public void update(Order order, String message) {
        System.out.println("[EMAIL] Отправка уведомления на email клиента:");
        System.out.println("   Заказ ID: " + order.getId());
        System.out.println("   Сообщение: " + message);
        System.out.println("   Статус: " + order.getStatus());
        System.out.println("   Маршрут: " + order.getFrom() + " → " + order.getTo());
        System.out.println("   ✓ Email отправлен!\n");
    }

    @Override
    public String getNotifierType() {
        return "EMAIL";
    }
}