package ru.practicum.service;

import org.junit.jupiter.api.Test;
import ru.practicum.exception.ManagerSaveException;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Test
    void testSaveAndLoadTasks() throws IOException {
        File tempFile = File.createTempFile("tasks_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        task.setId(1);
        task.setStatus(Status.NEW);
        manager.saveTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Количество задач должно быть равно 1");
        assertEquals(task, loadedManager.getAllTasks().getFirst(), "Задача должна корректно загружаться из файла");
    }

    @Test
    void testSaveAndLoadEpicsAndSubtasks() throws IOException {
        File tempFile = File.createTempFile("epics_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic 1", "Epic Description", Duration.ZERO, null);
        epic.setId(1);
        manager.saveEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), epic.getId());
        subtask.setSubtaskId(2);
        subtask.setStatus(Status.NEW);
        manager.saveSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllEpics().size(), "Количество эпиков должно быть равно 1");
        assertEquals(epic, loadedManager.getAllEpics().getFirst(), "Эпик должен корректно загружаться из файла");

        assertEquals(1, loadedManager.getAllSubtasks().size(), "Количество подзадач должно быть равно 1");
        assertEquals(subtask, loadedManager.getAllSubtasks().getFirst(), "Подзадача должна корректно загружаться из файла");
    }

    @Test
    void testSaveAndLoadEmptyManager() throws IOException {
        File tempFile = File.createTempFile("empty_test", ".csv");
        tempFile.deleteOnExit();

        new FileBackedTaskManager(tempFile);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    void testExceptionOnInvalidFile() {
        File invalidFile = new File("/invalid/path.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
            manager.saveTask(new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0)));
        }, "Ожидалось исключение ManagerSaveException при сохранении задачи в невалидный путь");
    }

    @Test
    void testEpicCalculationAfterLoad() throws IOException {
        File tempFile = File.createTempFile("epic_calculation_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic", "Description", Duration.ZERO, null);
        manager.saveEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 10, 0), epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Duration.ofHours(2), LocalDateTime.of(2025, 1, 1, 13, 0), epic.getId());
        manager.saveSubtask(subtask1);
        manager.saveSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllEpics().size(), "Количество эпиков должно быть равно 1");
        Epic loadedEpic = loadedManager.getAllEpics().getFirst();
        assertEquals(Duration.ofHours(3), loadedEpic.getDuration(), "Длительность эпика должна корректно рассчитываться");
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), loadedEpic.getStartTime(), "Начальное время эпика должно корректно рассчитываться");
    }

    @Test
    void testManagerSaveExceptionWithInvalidPath() {
        File invalidFile = new File("/invalid/path.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);
            Task task = new Task("Task", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
            manager.saveTask(task);
        }, "Ожидалось исключение ManagerSaveException при сохранении задачи в невалидный путь");
    }

    @Test
    void testLoadingFromCorruptedFile() throws IOException {
        // Создаем временный файл
        File corruptedFile = File.createTempFile("corrupted", ".csv");
        corruptedFile.deleteOnExit();

        // Записываем некорректные данные в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(corruptedFile))) {
            writer.write("Некорректные данные\n");
            writer.write("Еще одна строка без формата\n");
        }

        // Проверяем, что загрузка файла выбрасывает ManagerSaveException
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(corruptedFile),
                "Ожидалось исключение ManagerSaveException при загрузке повреждённого файла");
    }

    @Test
    void testSaveAndLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("multiple_tasks_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        task1.setId(1);
        task1.setStatus(Status.NEW);
        manager.saveTask(task1);

        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 1, 0));
        task2.setId(2);
        task2.setStatus(Status.IN_PROGRESS);
        manager.saveTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTasks().size(), "Количество задач должно быть равно 2");
        assertEquals(task1, loadedManager.getAllTasks().get(0), "Первая задача должна корректно загружаться");
        assertEquals(task2, loadedManager.getAllTasks().get(1), "Вторая задача должна корректно загружаться");
    }

    @Override
    FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("tasks_test.csv"));
    }
}