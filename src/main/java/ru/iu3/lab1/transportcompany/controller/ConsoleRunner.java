package ru.iu3.lab1.transportcompany.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.iu3.lab1.transportcompany.exception.OrderNotFoundException;
import ru.iu3.lab1.transportcompany.exception.VehicleNotAvailableException;
import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;
import ru.iu3.lab1.transportcompany.service.OrderService;
import ru.iu3.lab1.transportcompany.service.VehicleService;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {
    private final OrderService orderService;
    private final VehicleService vehicleService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleRunner(OrderService orderService, VehicleService vehicleService) {
        this.orderService = orderService;
        this.vehicleService = vehicleService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Транспортная компания ===\n");

        while (true) {
            printMenu();
            int choice = getIntInput("Выбор: ");

            if (!handleChoice(choice)) {
                break;
            }
        }
    }

    private void printMenu() {
        System.out.println("\n1. Создать заказ");
        System.out.println("2. Посчитать стоимость заказа");
        System.out.println("3. Назначить транспорт");
        System.out.println("4. Изменить статус заказа");
        System.out.println("5. Показать все заказы");
        System.out.println("6. Показать весь транспорт");
        System.out.println("0. Выход");
    }

    private boolean handleChoice(int choice) {
        switch (choice) {
            case 1 -> createOrder();
            case 2 -> calculatePrice();
            case 3 -> assignVehicle();
            case 4 -> updateStatus();
            case 5 -> showAllOrders();
            case 6 -> showAllVehicles();
            case 0 -> {
                System.out.println("Пока!");
                System.exit(0);
            }
            default -> System.out.println("Неверный выбор!");
        }
        return true;
    }

    private void createOrder() {
        System.out.print("Откуда: ");
        String from = scanner.nextLine();
        System.out.print("Куда: ");
        String to = scanner.nextLine();
        double weight = getDoubleInput("Вес (кг): ");

        Order order = orderService.createOrder(from, to, weight);
        System.out.println("✓ Заказ создан!");
        System.out.println("ID заказа: " + order.getId());
        System.out.println("Сохраните этот ID для дальнейших операций!");
    }

    private void calculatePrice() {
        String id = getInput("ID заказа: ");
        try {
            double price = orderService.calculatePrice(id);
            System.out.println("✓ Стоимость: " + price + " руб.");
        } catch (OrderNotFoundException e) {
            System.out.println("✗ Заказ не найден: " + id);
        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + getSimpleErrorMessage(e));
        }
    }

    private void assignVehicle() {
        String orderId = getInput("ID заказа: ");

        // Сначала проверяем, существует ли заказ
        try {
            orderService.calculatePrice(orderId); // просто проверяем существование
        } catch (OrderNotFoundException e) {
            System.out.println("✗ Заказ не найден: " + orderId);
            System.out.println("Сначала создайте заказ (пункт 1)");
            return;
        } catch (Exception e) {
            // игнорируем другие ошибки
        }

        System.out.println("\nДоступный транспорт:");
        showAllVehicles();
        String vehicleId = getInput("\nID транспорта (например v1, v2, v3): ");

        try {
            orderService.assignVehicle(orderId, vehicleId);
            System.out.println("✓ Транспорт назначен");
        } catch (OrderNotFoundException e) {
            System.out.println("✗ Заказ не найден: " + orderId);
        } catch (VehicleNotAvailableException e) {
            System.out.println("✗ Транспорт не найден или не подходит: " + vehicleId);
        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + getSimpleErrorMessage(e));
        }
    }

    private void updateStatus() {
        String orderId = getInput("ID заказа: ");

        // Сначала проверяем, существует ли заказ
        try {
            orderService.calculatePrice(orderId); // просто проверяем существование
        } catch (OrderNotFoundException e) {
            System.out.println("✗ Заказ не найден: " + orderId);
            System.out.println("Сначала создайте заказ (пункт 1)");
            return;
        } catch (Exception e) {
            // игнорируем другие ошибки
        }

        System.out.println("Доступные статусы: CREATED, ASSIGNED, IN_TRANSIT, DELIVERED");
        String statusStr = getInput("Новый статус (введите текстом): ");

        try {
            OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
            orderService.updateStatus(orderId, status);
            System.out.println("✓ Статус обновлен");
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Неверный статус! Доступны: CREATED, ASSIGNED, IN_TRANSIT, DELIVERED");
        } catch (OrderNotFoundException e) {
            System.out.println("✗ Заказ не найден: " + orderId);
        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + getSimpleErrorMessage(e));
        }
    }

    private void showAllOrders() {
        System.out.println("\n=== ВСЕ ЗАКАЗЫ ===");
        try {
            List<Order> orders = orderService.getAllOrders();
            if (orders.isEmpty()) {
                System.out.println("Заказов нет");
                return;
            }
            for (Order order : orders) {
                System.out.printf("ID: %s | %s → %s | %.2f кг | %s | Транспорт: %s%n",
                        order.getId(),
                        order.getFrom(),
                        order.getTo(),
                        order.getWeight(),
                        order.getStatus(),
                        order.getVehicleId() != null ? order.getVehicleId() : "не назначен");
            }
        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + getSimpleErrorMessage(e));
        }
    }

    private void showAllVehicles() {
        vehicleService.getAllVehicles().forEach(v ->
                System.out.println(v.getId() + " - " + v.getType() +
                        " (грузоподъемность: " + v.getCapacity() + " кг)"));
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите число!");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите число!");
            }
        }
    }

    // Извлекаем только сообщение об ошибке без полного пути
    private String getSimpleErrorMessage(Throwable e) {
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            return e.getClass().getSimpleName();
        }
        return message;
    }
}