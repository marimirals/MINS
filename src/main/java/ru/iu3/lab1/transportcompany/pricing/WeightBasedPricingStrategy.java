package ru.iu3.lab1.transportcompany.pricing;

import org.springframework.stereotype.Component;
import ru.iu3.lab1.transportcompany.model.Order;

@Component
public class WeightBasedPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(Order order) {
        // Базовый тариф: 10 руб за кг
        return order.getWeight() * 10;
    }
}