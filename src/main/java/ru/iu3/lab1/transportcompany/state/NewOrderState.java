package ru.iu3.lab1.transportcompany.state;

import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;

public class NewOrderState implements OrderState {

    @Override
    public void next(Order order) {
        // Проверяем, что транспорт назначен
        if (order.getVehicleId() == null || order.getVehicleId().isEmpty()) {
            throw new IllegalStateException("Нельзя перевести заказ в выполнение без назначенного транспорта!");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setState(new InProgressState());
        System.out.println("Заказ перешел в состояние: В ПУТИ");
    }

    @Override
    public void cancel(Order order) {
        // Новый заказ можно отменить
        order.setStatus(OrderStatus.CANCELLED);
        order.setState(new CancelledState());
        System.out.println("Заказ отменен");
    }

    @Override
    public String getName() {
        return "NEW";
    }

    @Override
    public boolean canCancel() {
        return true; // Новый заказ можно отменить
    }

    @Override
    public boolean canProceed() {
        return true; // Можно перейти в IN_PROGRESS
    }
}