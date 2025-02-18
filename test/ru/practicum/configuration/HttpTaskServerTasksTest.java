package ru.practicum.configuration;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.configuration.HttpTaskServerTest.HEADER_CONTENT_TYPE;
import static ru.practicum.configuration.HttpTaskServerTest.MIME_APPLICATION_JSON_UTF8;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpTaskServerTasksTest {
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private int taskId;

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
    void clearTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private int createTestTask() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025, 7, 1, 10, 0);
        String formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String taskJson = String.format("""
                {
                  "name": "New Task",
                  "description": "Task description",
                  "startTime": "%s",
                  "duration": "PT1H30M"
                }
                """, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка создания задачи!");

        // Парсим ID из ответа
        String responseBody = response.body();
        Task createdTask = gson.fromJson(responseBody, Task.class);
        int id = createdTask.getId();
        assertTrue(id > 0, "Сервер не вернул корректный ID!");
        return id;
    }

    private int createTestTaskOther() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025, 8, 1, 10, 0);
        String formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String taskJson = String.format("""
                {
                  "name": "New Task",
                  "description": "Task description",
                  "startTime": "%s",
                  "duration": "PT1H30M"
                }
                """, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка создания задачи!");

        // Парсим ID из ответа
        String responseBody = response.body();
        Task createdTask = gson.fromJson(responseBody, Task.class);
        int id = createdTask.getId();
        assertTrue(id > 0, "Сервер не вернул корректный ID!");
        return id;
    }

    @Test
    @Order(1)
    void testCreateTask() throws IOException, InterruptedException {
        taskId = createTestTask();
    }

    @Test
    @Order(2)
    void testGetAllTasks() throws IOException, InterruptedException {
        taskId = createTestTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения списка задач!");
        assertTrue(response.body().contains("New Task"), "Ответ не содержит ожидаемую задачу!");
    }

    @Test
    @Order(3)
    void testGetTaskById() throws IOException, InterruptedException {
        taskId = createTestTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при получении задачи!");
        assertTrue(response.body().contains("New Task"), "Ответ не содержит ожидаемую задачу!");
    }

    @Test
    @Order(4)
    void testUpdateTask() throws IOException, InterruptedException {
        taskId = createTestTask();

        LocalDateTime newStartTime = LocalDateTime.of(2025, 7, 10, 14, 0);
        String formattedStartTime = newStartTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String updatedTaskJson = String.format("""
                {
                  "id": %d,
                  "name": "Updated Task",
                  "description": "Updated description",
                  "startTime": "%s",
                  "duration": "PT2H"
                }
                """, taskId, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка обновления задачи!");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(getResponse.body().contains("Updated Task"), "Обновление задачи не применилось!");
    }

    @Test
    @Order(5)
    void testDeleteTaskById() throws IOException, InterruptedException {
        taskId = createTestTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка удаления задачи!");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Задача не была удалена!");
    }

    @Test
    @Order(6)
    void testDeleteAllTasks() throws IOException, InterruptedException {
        int firstTaskId = createTestTask();
        int secondTaskId = createTestTaskOther();

        // Отправляем DELETE-запрос для удаления всех задач
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка удаления всех задач!");

        // Проверяем, что список задач пуст
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", getResponse.body(), "После удаления список задач не пуст!");

        // Проверяем, что задачи больше недоступны по ID
        HttpRequest getTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + firstTaskId))
                .GET()
                .build();
        HttpResponse<String> getTaskResponse = client.send(getTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getTaskResponse.statusCode(), "Удаленная задача все еще доступна!");

        getTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + secondTaskId))
                .GET()
                .build();
        getTaskResponse = client.send(getTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getTaskResponse.statusCode(), "Удаленная задача все еще доступна!");
    }

    @Test
    @Order(7)
    void testCreateTaskWithOverlappingTime() throws IOException, InterruptedException {
        createTestTask(); // Создаем первую задачу

        // Попытка создать вторую задачу с таким же `startTime`
        LocalDateTime startTime = LocalDateTime.of(2025, 7, 1, 10, 0);
        String formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String overlappingTaskJson = String.format("""
            {
              "name": "Conflicting Task",
              "description": "This task has overlapping time",
              "startTime": "%s",
              "duration": "PT1H30M"
            }
            """, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(overlappingTaskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что сервер вернул 406 (конфликт времени)
        assertEquals(406, response.statusCode(), "Ожидался статус 406 при пересечении времени!");
    }
}