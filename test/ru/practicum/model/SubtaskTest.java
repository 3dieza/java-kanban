package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubtaskTest {
    @Test
    void testSubtaskCreation() {
        Subtask subtask = new Subtask("Subtask Name", "Subtask Description", 1);
        assertEquals("Subtask Name", subtask.getName(), "Название подзадачи должно совпадать");
        assertEquals("Subtask Description", subtask.getDescription(), "Описание подзадачи должно совпадать");
        assertEquals(1, subtask.getEpicId(), "ID эпика должен совпадать");
    }

    @Test
    void testSubtaskNullValues() {
        Subtask subtask = new Subtask(null, null, 1);
        assertNull(subtask.getName(), "Название должно быть null, если передан null");
        assertNull(subtask.getDescription(), "Описание должно быть null, если передан null");
    }
}