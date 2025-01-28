package ru.practicum.configuration;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        try {
            if ("GET".equalsIgnoreCase(method) && parts.length == 4 && "subtasks".equals(parts[3])) {
                int epicId = Integer.parseInt(parts[2]);
                handleGetSubtasksByEpicId(exchange, epicId);
            } else if ("GET".equalsIgnoreCase(method) && parts.length == 2) {
                handleGetAllEpics(exchange);
            } else if ("GET".equalsIgnoreCase(method) && parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                handleGetEpicById(exchange, id);
            } else if ("POST".equalsIgnoreCase(method) && parts.length == 2) {
                handleCreateOrUpdateEpic(exchange);
            } else if ("DELETE".equalsIgnoreCase(method) && parts.length == 2) {
                handleDeleteAllEpics(exchange);
            } else if ("DELETE".equalsIgnoreCase(method) && parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                handleDeleteEpicById(exchange, id);
            } else {
                sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, "{\"error\":\"Invalid Epic ID\"}", 400);
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();

        List<Map<String, Object>> responseList = epics.stream().map(epic -> {
            Map<String, Object> epicData = new HashMap<>();
            epicData.put("epicId", epic.getId());
            epicData.put("name", epic.getName());
            epicData.put("description", epic.getDescription());
            epicData.put("status", epic.getStatus());
            epicData.put("duration", epic.getDuration());
            epicData.put("startTime", epic.getStartTime());
            epicData.put("subtasks", epic.getSubtasks());
            return epicData;
        }).collect(Collectors.toList());

        sendText(exchange, gson.toJson(responseList), 200);
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            sendText(exchange, gson.toJson(epic), 200);
        } else {
            sendText(exchange, "{\"error\":\"Epic not found\"}", 404);
        }
    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Получен JSON: " + body);

        Epic epic = gson.fromJson(body, Epic.class);
        System.out.println("Парсинг успешен: " + epic);

        try {
            if (epic.getId() != null && epic.getId() != 0) {
                System.out.println("Установлен ID: " + epic.getId());
            } else {
                System.out.println("ID не передан, создаем новый эпик.");
            }

            if (epic.getId() != null && taskManager.getEpicById(epic.getId()) != null) {
                System.out.println("Обновление существующего эпика...");
                taskManager.updateEpic(epic);
                sendText(exchange, gson.toJson(epic), 200);
            } else {
                System.out.println("Создание нового эпика...");
                taskManager.saveEpic(epic);
                sendText(exchange, gson.toJson(epic), 201);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка обработки эпика: " + e.getMessage());
            sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 406);
        }
    }

    private void handleGetSubtasksByEpicId(HttpExchange exchange, int epicId) throws IOException {
        Epic epic = taskManager.getEpicById(epicId);
        if (epic != null) {
            List<Subtask> subtasks = taskManager.getAllSubtasksByEpic(epic);
            sendText(exchange, gson.toJson(subtasks), 200);
        } else {
            sendText(exchange, "{\"error\":\"Epic not found\"}", 404);
        }
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        sendText(exchange, "{\"message\":\"All epics deleted successfully\"}", 200);
    }

    private void handleDeleteEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            taskManager.deleteEpicById(id);
            sendText(exchange, "{\"message\":\"Epic deleted successfully\"}", 200);
        } else {
            sendText(exchange, "{\"error\":\"Epic not found\"}", 404);
        }
    }
}