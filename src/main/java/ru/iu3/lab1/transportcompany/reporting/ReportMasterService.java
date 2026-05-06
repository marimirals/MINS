package ru.iu3.lab1.transportcompany.reporting;

import org.springframework.stereotype.Service;
import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.repository.OrderRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GOD CLASS. Нарушает single responsibility, open closed. Делает выборку данных, валидацию, финансовые расчёты,
 * строковое форматирование, работу с файловой системой, логирование и обработку ошибок.
 */
@Service
public class ReportMasterService {

    private final OrderRepository orderRepository;

    // хардкод
    private static final String REPORT_DIR = "reports/";
    private static final double VAT_RATE = 0.20; // 20% НДС
    private static final int TABLE_WIDTH = 90;
    private static final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

    public ReportMasterService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void generateReport() {
        System.out.println("Генерация отчета по всем заказам.");

        // 1. Получение данных + валидация
        List<Order> orders;
        try {
            orders = orderRepository.findAll();
            if (orders == null || orders.isEmpty()) {
                System.out.println("База заказов пуста. Отчет не создан.");
                return;
            }
        } catch (Exception e) {
            // без обработки исключений
            System.out.println("Ошибка чтения репозитория: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 2. Расчёт НДС и форматирование таблицы
        StringBuilder report = new StringBuilder();
        report.append(String.format("%-36s | %-15s | %-15s | %-12s | %-12s | %-10s\n",
                "ID ЗАКАЗА", "МАРШРУТ", "ВЕС(КГ)", "ЦЕНА", "НДС(20%)", "СТАТУС"));
        report.append(SUB_SEPARATOR).append("\n");

        double totalVat = 0.0;
        int processedCount = 0;

        for (Order o : orders) {
            // расчёт в цикле форматирования
            double vat = o.getPrice() * VAT_RATE;
            totalVat += vat;

            String route = (o.getFrom() != null ? o.getFrom() : "?") + " → " +
                    (o.getTo() != null ? o.getTo() : "?");
            String status = o.getStatus() != null ? o.getStatus().toString() : "UNKNOWN";
            String safeId = o.getId();

            // Магические числа в форматировании (ANTI-PATTERN)
            report.append(String.format("%-36s | %-15s | %-15.1f | %-12.2f | %-12.2f | %-10s\n",
                    safeId, route, o.getWeight(), o.getPrice(), vat, status));
            processedCount++;
        }

        report.append(SUB_SEPARATOR).append("\n");
        report.append(String.format("Обработано заказов: %d\n", processedCount));
        report.append(String.format("Итого НДС к уплате: %.2f руб.\n", totalVat));
        report.append("Дата формирования: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "\n");

        // 3. Сохранение в файл
        saveToFile(report.toString());
    }

    // Метод сохранения, тоже тут
    private void saveToFile(String content) {
        // Создание директории внутри бизнес-метода
        File dir = new File(REPORT_DIR);
        if (!dir.exists()) dir.mkdirs();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = REPORT_DIR + "orders_report_" + timestamp + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            System.out.println("Отчет успешно сохранен: " + fileName);
            System.out.println("Находится по адресу: " + fileName);
        } catch (IOException e) {
            // ошибка без обработки
            System.out.println("Ошибка записи файла " + e.getMessage());
        }
    }
}