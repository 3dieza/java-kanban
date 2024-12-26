package ru.practicum.service;

import org.junit.jupiter.api.Test;
import ru.practicum.exception.ManagerSaveException;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void testSaveAndLoadTasks() throws IOException {
        File tempFile = File.createTempFile("tasks_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Task 1", "Description 1");
        task.setId(1);
        task.setStatus(Status.NEW);
        manager.saveTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(task, loadedManager.getAllTasks().getFirst());
    }

    @Test
    void testSaveAndLoadEpicsAndSubtasks() throws IOException {
        File tempFile = File.createTempFile("epics_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic 1", "Epic Description");
        epic.setId(1);
        manager.saveEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
        subtask.setSubtaskId(2);
        subtask.setStatus(Status.NEW);
        manager.saveSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(epic, loadedManager.getAllEpics().getFirst());

        assertEquals(1, loadedManager.getAllSubtasks().size());
        assertEquals(subtask, loadedManager.getAllSubtasks().getFirst());
    }

    @Test
    void testSaveAndLoadEmptyManager() throws IOException {
        File tempFile = File.createTempFile("empty_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testExceptionOnInvalidFile() {
        File invalidFile = new File("/invalid/path.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
            manager.saveTask(new Task("Task 1", "Description 1"));
        });
    }

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        // Создаем временный файл
        File tempFile = File.createTempFile("empty_test", ".csv");
        tempFile.deleteOnExit();

        // Создаем менеджер с временным файлом
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        // Загружаем из пустого файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что списки задач, эпиков и подзадач пусты
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testSaveAndLoadMultipleTasks() throws IOException {
        // Создаем временный файл
        File tempFile = File.createTempFile("multiple_tasks_test", ".csv");
        tempFile.deleteOnExit();

        // Создаем менеджер с временным файлом
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        // Добавляем несколько задач
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        task1.setStatus(Status.NEW);
        manager.saveTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        task2.setStatus(Status.IN_PROGRESS);
        manager.saveTask(task2);

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что задачи успешно загружены
        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(task1, loadedManager.getAllTasks().get(0));
        assertEquals(task2, loadedManager.getAllTasks().get(1));
    }
}