package ru.iu3.lab1.transportcompany.exception;
public abstract class TransportCompanyException extends RuntimeException {
    protected TransportCompanyException(String message) { super(message); }
    protected TransportCompanyException(String message, Throwable cause) { super(message, cause); }
}