package ru.iu3.lab1.transportcompany.exception;

public class OrderNotFoundException extends TransportCompanyException {
    public OrderNotFoundException(String id) { super("Order not found: " + id); }
}