package ru.practicum.configuration;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.service.FileBackedTaskManager;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.taskManager = taskManager;
        this.gson = Managers.getDefaultGson();
        initContext();
    }

    private void initContext() {
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP server started on port " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP server stopped.");
    }

    public static void main(String[] args) throws IOException {
        File file = new File("tasks.csv");

        // Загружаем данные из файла
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        // Создаем и запускаем сервер
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }
}