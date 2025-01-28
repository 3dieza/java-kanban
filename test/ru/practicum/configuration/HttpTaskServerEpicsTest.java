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
import ru.practicum.model.Epic;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpTaskServerEpicsTest {
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private int epicId;

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
    void clearEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private int createTestEpic() throws IOException, InterruptedException {
        String epicJson = """
                {
                  "name": "New Epic",
                  "description": "Epic description"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка создания эпика!");

        // Парсим ID из ответа
        String responseBody = response.body();
        Epic createdEpic = gson.fromJson(responseBody, Epic.class);
        int id = createdEpic.getId();
        assertTrue(id > 0, "Сервер не вернул корректный ID!");
        return id;
    }

    @Test
    @Order(1)
    void testCreateEpic() throws IOException, InterruptedException {
        epicId = createTestEpic();
    }

    @Test
    @Order(2)
    void testGetAllEpics() throws IOException, InterruptedException {
        epicId = createTestEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка получения списка эпиков!");
        assertTrue(response.body().contains("New Epic"), "Ответ не содержит ожидаемый эпик!");
    }

    @Test
    @Order(3)
    void testGetEpicById() throws IOException, InterruptedException {
        epicId = createTestEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при получении эпика!");
        assertTrue(response.body().contains("New Epic"), "Ответ не содержит ожидаемый эпик!");
    }

    @Test
    @Order(4)
    void testUpdateEpic() throws IOException, InterruptedException {
        epicId = createTestEpic();

        String updatedEpicJson = String.format("""
                {
                  "id": %d,
                  "name": "Updated Epic",
                  "description": "Updated description"
                }
                """, epicId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка обновления эпика!");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(getResponse.body().contains("Updated Epic"), "Обновление эпика не применилось!");
    }

    @Test
    @Order(5)
    void testDeleteEpicById() throws IOException, InterruptedException {
        epicId = createTestEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка удаления эпика!");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Эпик не был удален!");
    }

    @Test
    @Order(6)
    void testDeleteAllEpics() throws IOException, InterruptedException {
        int firstEpicId = createTestEpic();
        int secondEpicId = createTestEpic();

        // Отправляем DELETE-запрос для удаления всех эпиков
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка удаления всех эпиков!");

        // Проверяем, что список эпиков пуст
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", getResponse.body(), "После удаления список эпиков не пуст!");

        // Проверяем, что эпики больше недоступны по ID
        HttpRequest getEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + firstEpicId))
                .GET()
                .build();
        HttpResponse<String> getEpicResponse = client.send(getEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getEpicResponse.statusCode(), "Удаленный эпик все еще доступен!");

        getEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + secondEpicId))
                .GET()
                .build();
        getEpicResponse = client.send(getEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getEpicResponse.statusCode(), "Удаленный эпик все еще доступен!");
    }
}