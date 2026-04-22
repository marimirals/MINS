package ru.iu3.lab1.transportcompany.exception;

public abstract class TransportException extends RuntimeException {
    protected TransportException(String message) { super(message); }
    protected TransportException(String message, Throwable cause) { super(message, cause); }
}