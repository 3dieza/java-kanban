package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void testTaskCreation() {
        Task task = new Task("Task Name", "Task Description");
        assertEquals("Task Name", task.getName(), "Название задачи должно совпадать");
        assertEquals("Task Description", task.getDescription(), "Описание задачи должно совпадать");
    }
}