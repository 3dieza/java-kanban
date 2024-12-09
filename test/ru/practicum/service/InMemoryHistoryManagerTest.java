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
        task1.setId(1);
        task2.setId(1);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
    }

    @Test
    void testHistoryUnlimit() {
        Task task = new Task("Task", "Description ");
        for (int i = 1; i <= 11; i++) {
            historyManager.add(task);
            task.setId(i);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(11, history.size(), "История должна содержать не более 10 элементов");
    }

    @Test
    void testRemoveHistoryById() {

        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.getHistory();
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        assertEquals(1, historyManager.getHistory().size(), "История должна содержать не более 1 элементов");
    }
}