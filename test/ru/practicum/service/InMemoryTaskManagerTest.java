package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void testTasksEqualityById() {
        Task task1 = new Task("Task", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        Task task2 = new Task("Task", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void testSubtasksEqualityById() {
        Epic epic = new Epic("Epic", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask1 = new Subtask("Subtask", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0), epic.getId());
        Subtask subtask2 = new Subtask("Subtask", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0), epic.getId());
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым ID должны быть равны");
    }

    @Test
    void testPreventEpicSelfSubtask() {
        Epic epic = new Epic("Epic", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", Duration.ZERO, null, epic.getId());
        taskManager.saveSubtask(subtask);

        assertNotEquals(epic.getId(), subtask.getId(), "Эпик не должен быть подзадачей самого себя");
    }

    @Test
    void testPreventSubtaskBeingItsOwnEpic() {
        Subtask subtask = new Subtask("Subtask", "Description", Duration.ZERO, null, 1);
        subtask.setId(1);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> subtask.setEpicId(1));

        assertEquals("Подзадача не может ссылаться на свой же эпик.", thrown.getMessage());
    }

    @Test
    void testGetDefaultManagerInstances() {
        TaskManager manager = Managers.getDefaultTaskManager();
        assertNotNull(manager, "TaskManager должен быть проинициализирован");
        assertInstanceOf(InMemoryTaskManager.class, manager, "TaskManager должен быть экземпляром InMemoryTaskManager");

        HistoryManager history = Managers.getDefaultHistoryManager();
        assertNotNull(history, "HistoryManager должен быть проинициализирован");
        assertInstanceOf(InMemoryHistoryManager.class, history, "HistoryManager должен быть экземпляром InMemoryHistoryManager");
    }

    @Test
    void testSaveAndRetrieveTasksById() {
        Task task = new Task("Task", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        taskManager.saveTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask, "Задача должна быть доступна по ID");
    }

    @Test
    void testGeneratedAndSpecifiedIdTasksConflict() {
        Task generatedTask = new Task("Generated Task", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        taskManager.saveTask(generatedTask);

        Task specifiedTask = new Task("Specified Task", "Description", Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 0, 0));
        specifiedTask.setId(generatedTask.getId());
        taskManager.saveTask(specifiedTask);

        assertNotEquals(generatedTask, specifiedTask, "Задачи с разными ID не должны конфликтовать");
    }

    @Test
    void testEpicStatusCalculation() {
        Epic epic = new Epic("Epic Test", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        // Создание подзадач
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ZERO, null, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ZERO, null, epic.getId());
        taskManager.saveSubtask(subtask1);
        taskManager.saveSubtask(subtask2);

        // Граничное условие a: Все подзадачи со статусом NEW
        assertEquals(Status.NEW, epic.getStatus(), "Все подзадачи NEW - статус эпика должен быть NEW");

        // Граничное условие b: Все подзадачи со статусом DONE
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.DONE, epic.getStatus(), "Все подзадачи DONE - статус эпика должен быть DONE");

        // Граничное условие c: Подзадачи со статусами NEW и DONE
        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Подзадачи со статусами NEW и DONE - статус эпика должен быть IN_PROGRESS");

        // Граничное условие d: Подзадачи со статусом IN_PROGRESS
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Все подзадачи IN_PROGRESS - статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void testTaskIntervalsOverlap() {
        // Создаем задачу с заданным интервалом
        Task task1 = new Task("Task 1", "Description", Duration.ofHours(1), LocalDateTime.now());
        taskManager.saveTask(task1);

        // Создаем пересекающуюся задачу
        Task task2 = new Task("Task 2", "Description", Duration.ofHours(1), task1.getStartTime().plusMinutes(30));

        // Проверяем, что сохранение вызывает исключение
        assertThrows(IllegalArgumentException.class, () -> taskManager.saveTask(task2),
                "Пересекающиеся задачи не должны сохраняться.");

        // Проверка подзадач
        Epic epic = new Epic("Epic", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofHours(2), LocalDateTime.now(), epic.getId());
        taskManager.saveSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofHours(1), subtask1.getStartTime().plusMinutes(30), epic.getId());
        assertThrows(IllegalArgumentException.class, () -> taskManager.saveSubtask(subtask2),
                "Пересекающиеся подзадачи не должны сохраняться.");
    }
}