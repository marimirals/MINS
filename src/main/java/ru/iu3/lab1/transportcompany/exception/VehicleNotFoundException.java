package ru.iu3.lab1.transportcompany.exception;

public class VehicleNotFoundException extends TransportException {
    public VehicleNotFoundException(String id) { super("Vehicle not found: " + id); }
}