package ru.practicum.configuration;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
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
public class HttpTaskServerSubtasksTest {
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private int epicId;
    private int subtaskId;

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
    void clearSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        epicId = createTestEpic();
    }

    private int createTestEpic() throws IOException, InterruptedException {
        String epicJson = """
                {
                  "name": "Test Epic",
                  "description": "Epic for subtasks"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic createdEpic = gson.fromJson(response.body(), Epic.class);
        return createdEpic.getId();
    }

    private int createTestSubtask() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025, 7, 1, 10, 0);
        String formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String subtaskJson = String.format("""
                {
                  "name": "New Subtask",
                  "description": "Subtask description",
                  "epicId": %d,
                  "startTime": "%s",
                  "duration": "PT1H30M"
                }
                """, epicId, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка создания сабтаска!");

        Subtask createdSubtask = gson.fromJson(response.body(), Subtask.class);
        return createdSubtask.getId();
    }

    private void createTestSubtaskOther() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025, 9, 1, 10, 0);
        String formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String subtaskJson = String.format("""
                {
                  "name": "New Subtask",
                  "description": "Subtask description",
                  "epicId": %d,
                  "startTime": "%s",
                  "duration": "PT1H30M"
                }
                """, epicId, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка создания сабтаска!");
    }


    @Test
    @Order(1)
    void testCreateSubtask() throws IOException, InterruptedException {
        subtaskId = createTestSubtask();
    }

    @Test
    @Order(2)
    void testGetAllSubtasks() throws IOException, InterruptedException {
        subtaskId = createTestSubtask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения списка сабтасков!");
        assertTrue(response.body().contains("New Subtask"), "Ответ не содержит ожидаемый сабтаск!");
    }

    @Test
    @Order(3)
    void testGetSubtaskById() throws IOException, InterruptedException {
        subtaskId = createTestSubtask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при получении сабтаска!");
        assertTrue(response.body().contains("New Subtask"), "Ответ не содержит ожидаемый сабтаск!");
    }

    @Test
    @Order(4)
    void testUpdateSubtask() throws IOException, InterruptedException {
        subtaskId = createTestSubtask();

        LocalDateTime newStartTime = LocalDateTime.of(2025, 7, 10, 14, 0);
        String formattedStartTime = newStartTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String updatedSubtaskJson = String.format("""
                {
                  "id": %d,
                  "name": "Updated Subtask",
                  "description": "Updated description",
                  "epicId": %d,
                  "startTime": "%s",
                  "duration": "PT2H"
                }
                """, subtaskId, epicId, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка обновления сабтаска!");
    }

    @Test
    @Order(5)
    void testDeleteSubtaskById() throws IOException, InterruptedException {
        subtaskId = createTestSubtask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка удаления сабтаска!");
    }

    @Test
    @Order(6)
    void testDeleteAllSubtasks() throws IOException, InterruptedException {
        createTestSubtask();
        createTestSubtaskOther();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка удаления всех сабтасков!");
    }

    @Test
    @Order(7)
    void testCreateSubtaskWithOverlappingTime() throws IOException, InterruptedException {
        createTestSubtask(); // Создаем первую сабтаску

        // Попытка создать вторую сабтаску с таким же `startTime`
        LocalDateTime startTime = LocalDateTime.of(2025, 7, 1, 10, 0);
        String formattedStartTime = startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String overlappingSubtaskJson = String.format("""
            {
              "name": "Conflicting Subtask",
              "description": "This subtask has overlapping time",
              "epicId": %d,
              "startTime": "%s",
              "duration": "PT1H30M"
            }
            """, epicId, formattedStartTime);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(overlappingSubtaskJson))
                .header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что сервер вернул 406 (конфликт времени)
        assertEquals(406, response.statusCode(), "Ожидался статус 406 при пересечении времени!");
    }
}