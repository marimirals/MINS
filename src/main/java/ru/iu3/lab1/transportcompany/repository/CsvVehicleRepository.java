package ru.iu3.lab1.transportcompany.repository;

import org.springframework.stereotype.Repository;
import ru.iu3.lab1.transportcompany.model.Vehicle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CsvVehicleRepository implements VehicleRepository {
    private final Path FILE = Paths.get("vehicles.csv");

    public CsvVehicleRepository() {
        try {
            if (!Files.exists(FILE)) {
                Files.createFile(FILE);
                // Добавим несколько тестовых транспортных средств
                save(new Vehicle("v1", "TRUCK", 1000));
                save(new Vehicle("v2", "VAN", 500));
                save(new Vehicle("v3", "CAR", 200));
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл vehicles.csv", e);
        }
    }

    @Override
    public void save(Vehicle vehicle) {
        List<String> lines = readAllLines();
        String newLine = convertToCsv(vehicle);

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length > 0 && parts[0].equals(vehicle.getId())) {
                lines.set(i, newLine);
                writeAllLines(lines);
                return;
            }
        }

        lines.add(newLine);
        writeAllLines(lines);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return readAllLines().stream()
                .map(this::convertFromCsv)
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Vehicle> findAll() {
        return readAllLines().stream()
                .map(this::convertFromCsv)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Vehicle> findByType(String type) {
        return readAllLines().stream()
                .map(this::convertFromCsv)
                .filter(v -> v.getType().equalsIgnoreCase(type))
                .findFirst();
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

    private String convertToCsv(Vehicle vehicle) {
        return String.format("%s,%s,%.2f",
                vehicle.getId(),
                vehicle.getType(),
                vehicle.getCapacity());
    }

    private Vehicle convertFromCsv(String line) {
        String[] parts = line.split(",");
        Vehicle vehicle = new Vehicle();
        vehicle.setId(parts[0]);
        vehicle.setType(parts[1]);
        vehicle.setCapacity(Double.parseDouble(parts[2]));
        return vehicle;
    }
}