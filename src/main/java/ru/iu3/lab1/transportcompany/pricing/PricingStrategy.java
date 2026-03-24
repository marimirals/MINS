package ru.iu3.lab1.transportcompany.pricing;

import ru.iu3.lab1.transportcompany.model.Order;

public interface PricingStrategy {
    double calculate(Order order);
}
