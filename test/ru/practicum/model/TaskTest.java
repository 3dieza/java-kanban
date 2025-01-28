package ru.practicum.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskTest {
    @Test
    void testTaskCreation() {
        Task task = new Task("Task Name", "Task Description", Duration.ofMinutes(90), LocalDateTime.of(2025, 1, 1, 9, 0));
        assertEquals("Task Name", task.getName());
        assertEquals("Task Description", task.getDescription());
        assertEquals(Duration.ofMinutes(90), task.getDuration());
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 0), task.getStartTime());
    }

    @Test
    void testTaskNullValues() {
        Task task = new Task(null, null,null,null);
        assertNull(task.getName(), "Название должно быть null, если передан null");
        assertNull(task.getDescription(), "Описание должно быть null, если передан null");
        assertNull(task.getDuration(),"duration is null");
        assertNull(task.getStartTime(),"startTime is null");
    }
}