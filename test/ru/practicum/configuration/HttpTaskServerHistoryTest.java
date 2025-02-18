package ru.practicum.configuration;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.practicum.model.Task;
import ru.practicum.service.InMemoryHistoryManager;
import ru.practicum.service.InMemoryTaskManager;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.configuration.HttpTaskServerTest.HEADER_CONTENT_TYPE;
import static ru.practicum.configuration.HttpTaskServerTest.MIME_APPLICATION_JSON_UTF8;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpTaskServerHistoryTest {
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private int taskId1;
    private int taskId2;

    @BeforeAll
    void setUp() throws IOException {
        TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
        this.gson = Managers.getDefaultGson();
    }

    @AfterAll
    void tearDown() {
        taskServer.stop();
    }

    @BeforeEach
    void clearHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        taskId1 = createTestTask("Task 1", LocalDateTime.of(2025, 7, 1, 10, 0));
        taskId2 = createTestTask("Task 2", LocalDateTime.of(2025, 7, 2, 12, 0));
    }

    private int createTestTask(String name, LocalDateTime startTime) throws IOException, InterruptedException {
        String formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String taskJson = String.format("""
                {
                  "name": "%s",
                  "description": "Task description",
                  "startTime": "%s",
                  "duration": "PT1H"
                }
                """, name, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка создания задачи!");

        Task createdTask = gson.fromJson(response.body(), Task.class);
        return createdTask.getId();
    }

    @Test
    @Order(1)
    void testAddTasksToHistory() throws IOException, InterruptedException {
        // Запрос на просмотр задач (добавление в историю)
        viewTask(taskId1);
        viewTask(taskId2);

        // Получение истории
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения истории!");

        // Проверяем, что обе задачи есть в истории
        assertTrue(response.body().contains("Task 1"), "История не содержит первую задачу!");
        assertTrue(response.body().contains("Task 2"), "История не содержит вторую задачу!");
    }

    private void viewTask(int taskId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}