package ru.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void testEpicCreation() {
        Epic epic = new Epic("Epic Name", "Epic Description");
        assertEquals("Epic Name", epic.getName(), "Название эпика должно совпадать");
        assertEquals("Epic Description", epic.getDescription(), "Описание эпика должно совпадать");
        assertEquals(Status.NEW, epic.getStatus(), "Статус нового эпика должен быть NEW");
    }
}