package ru.practicum.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddAndRetrieveHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "История должна содержать 3 задачи");
    }

    @Test
    void testHistoryLimit() {
        for (int i = 1; i <= 11; i++) {
            historyManager.add(new Task("Task " + i, "Description " + i));
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать не более 10 элементов");
    }
}