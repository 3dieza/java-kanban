package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void testSubtaskCreation() {
        Subtask subtask = new Subtask("Subtask Name", "Subtask Description", 1);
        assertEquals("Subtask Name", subtask.getName(), "Название подзадачи должно совпадать");
        assertEquals("Subtask Description", subtask.getDescription(), "Описание подзадачи должно совпадать");
        assertEquals(1, subtask.getEpicId(), "ID эпика должен совпадать");
    }
}