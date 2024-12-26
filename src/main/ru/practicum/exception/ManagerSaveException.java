package ru.practicum.exception;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String description, IOException e) {
    }
}
