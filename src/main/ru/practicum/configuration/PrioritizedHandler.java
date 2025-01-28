package ru.practicum.configuration;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.model.Task;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.util.List;

class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                handleGetPrioritizedTasks(exchange);
            } else {
                sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        // Получаем приоритетные задачи из TaskManager
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        // Формируем JSON-ответ
        String jsonResponse = gson.toJson(prioritizedTasks);

        // Отправляем успешный ответ с кодом 200
        sendText(exchange, jsonResponse, 200);
    }
}