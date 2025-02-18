package ru.practicum.configuration;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.model.Status;
import ru.practicum.model.Task;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        try {
            if ("GET".equalsIgnoreCase(method) && parts.length == 2) {
                handleGetAllTasks(exchange);
            } else if ("GET".equalsIgnoreCase(method) && parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                handleGetTaskById(exchange, id);
            } else if ("POST".equalsIgnoreCase(method) && parts.length == 2) {
                handleCreateOrUpdateTask(exchange);
            } else if ("DELETE".equalsIgnoreCase(method) && parts.length == 2) {
                handleDeleteAllTasks(exchange);
            } else if ("DELETE".equalsIgnoreCase(method) && parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                handleDeleteTaskById(exchange, id);
            } else {
                sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "{\"error\":\"Invalid Task ID\"}", 400);
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());
        sendText(exchange, response, 200);
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            sendText(exchange, gson.toJson(task), 200);
        } else {
            sendText(exchange, "{\"error\":\"Task not found\"}", 404);
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        try {
            if (task.getId() == null) {
                // Если ID отсутствует, создаем новую задачу
                taskManager.saveTask(task);
                sendText(exchange, gson.toJson(task), 201);
            } else {
                // Если ID указан, проверяем, существует ли задача
                taskManager.updateTask(task);
                sendText(exchange, gson.toJson(task), 200);
            }
        } catch (IllegalArgumentException e) {
            sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 406);
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendText(exchange, "{\"message\":\"All tasks deleted successfully\"}", 200);
    }

    private void handleDeleteTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            taskManager.deleteTaskById(id);
            sendText(exchange, "{\"message\":\"Task deleted successfully\"}", 200);
        } else {
            sendText(exchange, "{\"error\":\"Task not found\"}", 404);
        }
    }
}