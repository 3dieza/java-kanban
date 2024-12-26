package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EpicTest {
    @Test
    void testEpicCreation() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        assertEquals("Epic Name", epic.getName(), "Название эпика должно совпадать");
        assertEquals("Epic Description", epic.getDescription(), "Описание эпика должно совпадать");
        assertEquals(Status.NEW, epic.getStatus(), "Статус нового эпика должен быть NEW");
    }

    @Test
    void testEpicNullValues() {
        Epic epic = new Epic(null, null);
        assertNull(epic.getName(), "Название должно быть null, если передан null");
        assertNull(epic.getDescription(), "Описание должно быть null, если передан null");
    }
}