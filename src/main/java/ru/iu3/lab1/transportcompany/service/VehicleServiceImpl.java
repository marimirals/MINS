package ru.iu3.lab1.transportcompany.service;

import org.springframework.stereotype.Service;
import ru.iu3.lab1.transportcompany.exception.VehicleNotAvailableException;
import ru.iu3.lab1.transportcompany.model.Order;
import ru.iu3.lab1.transportcompany.model.Vehicle;
import ru.iu3.lab1.transportcompany.repository.VehicleRepository;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle getVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotAvailableException("Vehicle not found: " + id));
    }

    @Override
    public Vehicle findAvailableVehicle(double weight) {
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getCapacity() >= weight)
                .findFirst()
                .orElseThrow(() -> new VehicleNotAvailableException(
                        "No vehicle available for weight: " + weight));
    }
}