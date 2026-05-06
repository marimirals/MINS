package ru.iu3.lab1.transportcompany.pricing;

import org.springframework.stereotype.Component;
import ru.iu3.lab1.transportcompany.model.Order;

import java.util.HashMap;
import java.util.Map;

@Component
public class PricingContext {
    private final Map<String, PricingStrategy> strategies = new HashMap<>();
    private PricingStrategy currentStrategy;

    public PricingContext(WeightBasedPricingStrategy weightStrategy,
                          PriorityPricingStrategy priorityStrategy) {
        strategies.put("weight", weightStrategy);
        strategies.put("priority", priorityStrategy);
        this.currentStrategy = weightStrategy; // по умолчанию
    }

    public double calculatePrice(Order order) {
        return currentStrategy.calculate(order);
    }

    public void setStrategy(String strategyType) {
        PricingStrategy strategy = strategies.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("Неизвестная стратегия: " + strategyType);
        }
        this.currentStrategy = strategy;
    }

    public String getCurrentStrategyName() {
        if (currentStrategy instanceof WeightBasedPricingStrategy) {
            return "По весу";
        } else if (currentStrategy instanceof PriorityPricingStrategy) {
            return "Приоритетная";
        }
        return "Неизвестно";
    }
}