package ru.practicum.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String description, Throwable cause) {
        super(description, cause);
    }
}
