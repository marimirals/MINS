package ru.iu3.lab1.transportcompany.service;

import ru.iu3.lab1.transportcompany.model.Vehicle;
import java.util.List;

public interface VehicleService {
    List<Vehicle> getAllVehicles();
    Vehicle getVehicleById(String id);
}