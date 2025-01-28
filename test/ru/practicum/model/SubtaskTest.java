package ru.practicum.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubtaskTest {
    @Test
    void testSubtaskCreation() {
        Subtask subtask = new Subtask("Subtask Name", "Subtask Description", Duration.ofHours(2), LocalDateTime.of(2025, 1, 1, 10, 0), 1);
        assertEquals("Subtask Name", subtask.getName());
        assertEquals("Subtask Description", subtask.getDescription());
        assertEquals(1, subtask.getEpicId());
        assertEquals(Duration.ofHours(2), subtask.getDuration());
    }

    @Test
    void testSubtaskNullValues() {
        Subtask subtask = new Subtask(null, null, null, null, 1);
        assertNull(subtask.getName(), "Название должно быть null, если передан null");
        assertNull(subtask.getDescription(), "Описание должно быть null, если передан null");
        assertNull(subtask.getDuration(),"duration is null");
        assertNull(subtask.getStartTime(),"startTime is null");
    }
}