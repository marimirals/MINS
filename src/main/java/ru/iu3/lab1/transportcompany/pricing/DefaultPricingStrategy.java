package ru.iu3.lab1.transportcompany.pricing;

import org.springframework.stereotype.Component;
import ru.iu3.lab1.transportcompany.model.Order;

@Component
public class DefaultPricingStrategy implements PricingStrategy {

    @Override
    public double calculate(Order order) {
        return order.getWeight() * 10; // базовый тариф
    }
}
