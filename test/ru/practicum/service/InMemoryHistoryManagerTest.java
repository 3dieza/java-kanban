package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testEmptyHistory() {
        // Проверяем, что история изначально пуста
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой изначально");
    }

    @Test
    void testAddAndRetrieveHistory() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertEquals(task1, history.get(0), "Первая задача в истории должна совпадать");
        assertEquals(task2, history.get(1), "Вторая задача в истории должна совпадать");
    }

    @Test
    void testDuplicateHistoryEntry() {
        Task task = new Task("Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "История не должна содержать дубликатов");
    }

    @Test
    void testRemoveHistoryById() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления");
        assertEquals(task2, history.getFirst(), "Оставшаяся задача должна совпадать с ожидаемой");
    }

    @Test
    void testRemoveHistoryFromPositions() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ZERO, null);
        Task task2 = new Task("Task 2", "Description 2", Duration.ZERO, null);
        Task task3 = new Task("Task 3", "Description 3", Duration.ZERO, null);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаление с начала
        historyManager.remove(task1.getId());
        assertEquals(2, historyManager.getHistory().size(), "После удаления первой задачи размер истории должен уменьшиться");
        assertEquals(task2, historyManager.getHistory().getFirst(), "После удаления первой задачи второй должна стать первой");

        // Удаление из середины
        historyManager.remove(task2.getId());
        assertEquals(1, historyManager.getHistory().size(), "После удаления второй задачи должна остаться одна задача");
        assertEquals(task3, historyManager.getHistory().getFirst(), "После удаления второй задачи третья должна стать первой");

        // Удаление с конца
        historyManager.remove(task3.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой после удаления всех задач");
    }

    @Test
    void testRemoveNonexistentTask() {
        historyManager.remove(999); // Удаляем задачу с ID, которого нет
        assertTrue(historyManager.getHistory().isEmpty(), "История должна оставаться пустой после попытки удаления несуществующей задачи");
    }

    @Test
    void testAddNullTask() {
        assertThrows(IllegalArgumentException.class,
                () -> historyManager.add(null), "Добавление null должно вызывать исключение");
    }

    @Test
    void testHistoryOrder() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        Task task3 = new Task("Task 3", "Description 3", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать три задачи");
        assertEquals(task1, history.get(0), "Первая задача должна совпадать с добавленной первой");
        assertEquals(task2, history.get(1), "Вторая задача должна совпадать с добавленной второй");
        assertEquals(task3, history.get(2), "Третья задача должна совпадать с добавленной третьей");
    }
}