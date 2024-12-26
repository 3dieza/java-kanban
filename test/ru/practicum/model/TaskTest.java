package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskTest {
    @Test
    void testTaskCreation() {
        Task task = new Task("Task Name", "Task Description");
        assertEquals("Task Name", task.getName(), "Название задачи должно совпадать");
        assertEquals("Task Description", task.getDescription(), "Описание задачи должно совпадать");
    }

    @Test
    void testTaskNullValues() {
        Task task = new Task(null, null);
        assertNull(task.getName(), "Название должно быть null, если передан null");
        assertNull(task.getDescription(), "Описание должно быть null, если передан null");
    }
}