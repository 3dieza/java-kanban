package ru.practicum.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpTaskServerPrioritizedTasksTest {
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeAll
    void setUp() throws IOException {
        TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();

        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    @AfterAll
    void tearDown() {
        taskServer.stop();
    }

    @BeforeEach
    void clearTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void createTestTask(String name, Duration duration, LocalDateTime startTime) throws IOException, InterruptedException {
        Task task = new Task(name, "Test Description", duration, startTime);
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка создания задачи!");

        Task createdTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(createdTask, "Ответ не содержит созданную задачу!");
    }

    @Test
    @Order(1)
    void testGetPrioritizedTasksEmpty() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения приоритетного списка!");
        assertEquals("[]", response.body(), "При отсутствии задач список должен быть пустым!");
    }

    @Test
    @Order(2)
    void testGetPrioritizedTasksWithTasks() throws IOException, InterruptedException {
        createTestTask("Task1", Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 1, 10, 0));
        createTestTask("Task2", Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 1, 9, 0));
        createTestTask("Task3", Duration.ofMinutes(90), LocalDateTime.of(2025, 7, 1, 11, 0));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения приоритетного списка!");

        List<Task> prioritizedTasks = List.of(gson.fromJson(response.body(), Task[].class));
        assertEquals(3, prioritizedTasks.size(), "Ожидалось 3 задачи в списке!");
        assertEquals("Task2", prioritizedTasks.getFirst().getName(), "Приоритетный список должен быть отсортирован!");
    }

    @Test
    @Order(3)
    void testGetPrioritizedTasksSorting() throws IOException, InterruptedException {
        createTestTask("Task A", Duration.ofMinutes(45), LocalDateTime.of(2025, 7, 1, 9, 0));
        createTestTask("Task B", Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 1, 8, 0));
        createTestTask("Task C", Duration.ofMinutes(90), LocalDateTime.of(2025, 7, 1, 10, 0));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения приоритетного списка!");

        List<Task> prioritizedTasks = List.of(gson.fromJson(response.body(), Task[].class));
        assertEquals(3, prioritizedTasks.size(), "Ожидалось 3 задачи в списке!");
        assertEquals("Task B", prioritizedTasks.get(0).getName(), "Первой должна быть задача с наименьшим `startTime`!");
        assertEquals("Task A", prioritizedTasks.get(1).getName(), "Второй должна быть следующая по `startTime`!");
        assertEquals("Task C", prioritizedTasks.get(2).getName(), "Последней должна быть самая поздняя задача!");
    }

    @Test
    @Order(4)
    void testGetPrioritizedTasksWithOverlappingTasks() throws IOException, InterruptedException {
        // Создаем первую задачу (должна быть успешна)
        createTestTask("Task X", Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 1, 9, 0));

        // Пытаемся создать вторую задачу с пересечением
        Task conflictingTask = new Task("Task Y", "Overlapping Task", Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 1, 9, 30));
        String taskJson = gson.toJson(conflictingTask);

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что сервер ОТКЛОНИЛ пересечение
        assertEquals(406, createResponse.statusCode(), "Сервер должен отклонять пересечения!");

        // Проверяем, что приоритетный список содержит только первую задачу
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения приоритетного списка!");

        List<Task> prioritizedTasks = List.of(gson.fromJson(response.body(), Task[].class));
        assertEquals(1, prioritizedTasks.size(), "В списке должна остаться только первая задача!");
        assertEquals("Task X", prioritizedTasks.getFirst().getName(), "Оставленная задача должна быть первой созданной!");
    }
}