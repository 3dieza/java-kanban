package ru.practicum.configuration;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HistoryHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            handleGetHistory(exchange);
        } else {
            sendText(exchange, "{\"error\": \"Метод " + method + " не поддерживается\"}", 405);
        }
    }

    /**
     * Обрабатывает GET-запрос на получение истории задач.
     */
    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String responseJson = gson.toJson(taskManager.getHistory());
        sendText(exchange, responseJson, 200);
    }

    /**
     * Вспомогательный метод для отправки JSON-ответов.
     */
    private void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.getResponseBody().close();
    }
}