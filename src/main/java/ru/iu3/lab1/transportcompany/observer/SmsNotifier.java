package ru.iu3.lab1.transportcompany.observer;

import ru.iu3.lab1.transportcompany.model.Order;
import org.springframework.stereotype.Component;

@Component
public class SmsNotifier implements OrderObserver {

    @Override
    public void update(Order order, String message) {
        // SMS отправляем только при важных событиях (доставка/отмена)
        if (order.getStatus() == ru.iu3.lab1.transportcompany.model.OrderStatus.DELIVERED ||
                order.getStatus() == ru.iu3.lab1.transportcompany.model.OrderStatus.CANCELLED) {
            System.out.println("[SMS] Отправка SMS клиенту:");
            System.out.println("   Заказ ID: " + order.getId());
            System.out.println("   Текст: " + message);
            System.out.println("   ✓ SMS отправлено!\n");
        } else {
            System.out.println("📱 [SMS] Пропущено (не критичное изменение): " + order.getId());
        }
    }

    @Override
    public String getNotifierType() {
        return "SMS";
    }
}