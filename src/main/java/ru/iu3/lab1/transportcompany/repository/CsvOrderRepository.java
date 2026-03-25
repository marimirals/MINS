package ru.iu3.lab1.transportcompany.repository;

import org.springframework.stereotype.Repository;
import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.OrderStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CsvOrderRepository implements OrderRepository {
    private final Path FILE = Paths.get("orders.csv");

    public CsvOrderRepository() {
        try {
            if (!Files.exists(FILE)) {
                Files.createFile(FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл orders.csv", e);
        }
    }

    @Override
    public void save(Order order) {
        List<String> lines = readAllLines();
        String newLine = convertToCsv(order);

        // Если заказ с таким ID уже есть - обновляем
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length > 0 && parts[0].equals(order.getId())) {
                lines.set(i, newLine);
                writeAllLines(lines);
                return;
            }
        }

        // Иначе добавляем новый
        lines.add(newLine);
        writeAllLines(lines);
    }

    /* LLLLL - Liskov Substitution (сужение предусловий)
    public void save(Order order) {
        if (order.getWeight() > 1000) {
            throw new IllegalArgumentException("Слишком тяжёлый!");
        }
    }
    */

    @Override
    public Optional<Order> findById(String id) {
        return readAllLines().stream()
                .map(this::convertFromCsv)
                .filter(order -> order != null && order.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Order> findAll() {
        return readAllLines().stream()
                .map(this::convertFromCsv)
                .filter(order -> order != null)
                .collect(Collectors.toList());
    }

    private List<String> readAllLines() {
        try {
            return Files.readAllLines(FILE);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }

    private void writeAllLines(List<String> lines) {
        try {
            Files.write(FILE, lines);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи файла", e);
        }
    }

    private String convertToCsv(Order order) {
        // ВАЖНО: используем Locale.US чтобы вес записывался с ТОЧКОЙ, а не запятой
        return String.format(Locale.US, "%s,%s,%s,%.2f,%s,%s",
                order.getId(),
                order.getFrom() != null ? order.getFrom() : "",
                order.getTo() != null ? order.getTo() : "",
                order.getWeight(),
                order.getStatus().name(),
                order.getVehicleId() != null ? order.getVehicleId() : "");
    }

    private Order convertFromCsv(String line) {
        // Пропускаем пустые строки
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split(",");

        // Проверяем, что есть все необходимые поля (минимум 5)
        if (parts.length < 5) {
            System.err.println("⚠️  Пропущена некорректная строка (мало полей): " + line);
            return null;
        }

        try {
            Order order = new Order();
            order.setId(parts[0].trim());
            order.setFrom(parts[1].trim());
            order.setTo(parts[2].trim());

            // Парсим вес с точкой
            order.setWeight(Double.parseDouble(parts[3].trim()));

            // Парсим статус
            String statusStr = parts[4].trim();
            try {
                order.setStatus(OrderStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️  Неверный статус '" + statusStr + "' в строке: " + line);
                return null;
            }

            // vehicleId (может отсутствовать)
            if (parts.length > 5 && !parts[5].trim().isEmpty()) {
                order.setVehicleId(parts[5].trim());
            } else {
                order.setVehicleId(null);
            }

            return order;
        } catch (Exception e) {
            System.err.println("⚠️  Ошибка парсинга: " + line);
            System.err.println("   Причина: " + e.getMessage());
            return null;
        }
    }

/* IIIII - part2
    @Override
    public void sendEmail(Order order) {
        // заглушка
    }

    @Override
    public void generatePDF(Order order) {
        // заглушка
    }

    @Override
    public void deleteAll() {
        // заглушка
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return findAll().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void exportToExcel(String filename) {
        // заглушка
    }
*/
}