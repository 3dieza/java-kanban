package ru.practicum.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EpicTest {
    @Test
    void testEpicCreation() {
        Epic epic = new Epic("Epic Name", "Epic Description", Duration.ZERO, LocalDateTime.of(2023, 1, 1, 10, 0));
        assertEquals("Epic Name", epic.getName());
        assertEquals("Epic Description", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), epic.getStartTime());
    }

    @Test
    void testRecalculateFields() {
        Epic epic = new Epic("Epic", "Description", Duration.ZERO, null);
        epic.setId(1);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofHours(2), LocalDateTime.of(2025, 1, 1, 10, 0), epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofHours(3), LocalDateTime.of(2025, 1, 1, 12, 0), epic.getId());

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(Duration.ofHours(5), epic.getDuration());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), epic.getStartTime());
    }

    @Test
    void testEpicNullValues() {
        Epic epic = new Epic(null, null, null, null);
        assertNull(epic.getName(), "Название должно быть null, если передан null");
        assertNull(epic.getDescription(), "Описание должно быть null, если передан null");
        assertNull(epic.getDuration(),"duration is null");
        assertNull(epic.getStartTime(),"startTime is null");
    }
}