package ru.iu3.lab1.transportcompany.pricing;

import org.springframework.stereotype.Component;
import ru.iu3.lab1.transportcompany.model.Order;

@Component
public class WeightBasedPricingStrategy implements PricingStrategy {

    // ️ LLLLL - "наследник может исп вместо родителя"
    // нарушает, тк PricingStrategy ожидает любой вес, а WeightBasedPricingStrategy -> нельзя заменить родителя наследником
    /*
     if (order.getWeight() < 5) {
         throw new IllegalArgumentException("LSP VIOLATION: Вес меньше 5 кг не обслуживается");
     }
    */

    @Override
    public double calculate(Order order) {
        // Базовый тариф: 10 руб за кг
        return order.getWeight() * 10;
    }
}