package ru.practicum.configuration;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String MIME_APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    /**
     * Отправка текста с указанным HTTP-статусом.
     *
     * @param exchange объект HttpExchange.
     * @param text     текст ответа.
     * @param status   HTTP-статус.
     * @throws IOException в случае ошибок ввода-вывода.
     */
    protected void sendText(HttpExchange exchange, String text, int status) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8);
        exchange.sendResponseHeaders(status, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }
}