package ru.iu3.lab1.transportcompany.repository;

import ru.iu3.lab1.transportcompany.model.Vehicle;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    void save(Vehicle vehicle);
    Optional<Vehicle> findById(String id);
    List<Vehicle> findAll();
    Optional<Vehicle> findByType(String type);
}