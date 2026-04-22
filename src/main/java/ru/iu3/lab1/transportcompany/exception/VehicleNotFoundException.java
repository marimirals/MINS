package ru.iu3.lab1.transportcompany.exception;

public class VehicleNotFoundException extends TransportCompanyException {
    public VehicleNotFoundException(String id) { super("Vehicle not found: " + id); }
}