package ru.practicum.configuration;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

import ru.practicum.service.InMemoryHistoryManager;
import ru.practicum.service.InMemoryTaskManager;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String MIME_APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
        this.gson = Managers.getDefaultGson();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("New Task", "Task description", Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertFalse(manager.getAllTasks().isEmpty(), "Задача не была добавлена");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "First task", Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Second task", Duration.ofMinutes(25), LocalDateTime.now().plusHours(1));
        manager.saveTask(task1);
        manager.saveTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"), "Первая задача отсутствует в ответе");
        assertTrue(response.body().contains("Task 2"), "Вторая задача отсутствует в ответе");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task to delete", "To be removed", Duration.ofMinutes(10), LocalDateTime.now());
        manager.saveTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty(), "Задача не была удалена");
    }
}