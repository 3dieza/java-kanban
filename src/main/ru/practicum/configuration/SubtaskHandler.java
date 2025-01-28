package ru.practicum.configuration;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.model.Subtask;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        try {
            if ("GET".equalsIgnoreCase(method) && parts.length == 2) {
                handleGetAllSubtasks(exchange);
            } else if ("GET".equalsIgnoreCase(method) && parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                handleGetSubtaskById(exchange, id);
            } else if ("POST".equalsIgnoreCase(method) && parts.length == 2) {
                handleCreateOrUpdateSubtask(exchange);
            } else if ("DELETE".equalsIgnoreCase(method) && parts.length == 2) {
                handleDeleteAllSubtasks(exchange);
            } else if ("DELETE".equalsIgnoreCase(method) && parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                handleDeleteSubtaskById(exchange, id);
            } else {
                sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "{\"error\":\"Invalid Subtask ID\"}", 400);
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtasks());
        sendText(exchange, response, 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask != null) {
            sendText(exchange, gson.toJson(subtask), 200);
        } else {
            sendText(exchange, "{\"error\":\"Subtask not found\"}", 404);
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() == null) {
                // Если ID отсутствует, создаем новую подзадачу
                taskManager.saveSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 201);
            } else {
                // Если ID указан, проверяем, существует ли подзадача
                taskManager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 200);
            }
        } catch (IllegalArgumentException e) {
            sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 406);
        }
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        sendText(exchange, "{\"message\":\"All subtasks deleted successfully\"}", 200);
    }

    private void handleDeleteSubtaskById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask != null) {
            taskManager.deleteSubtaskById(id);
            sendText(exchange, "{\"message\":\"Subtask deleted successfully\"}", 200);
        } else {
            sendText(exchange, "{\"error\":\"Subtask not found\"}", 404);
        }
    }
}