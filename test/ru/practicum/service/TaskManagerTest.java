package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void testSaveAndRetrieveTask() {
        Task task = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.saveTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()), "Задача должна корректно сохраняться и извлекаться.");
    }

    @Test
    void testSaveAndRetrieveEpic() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпик должен корректно сохраняться и извлекаться.");
    }

    @Test
    void testSaveAndRetrieveSubtask() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
        taskManager.saveSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадача должна корректно сохраняться и извлекаться.");
        assertTrue(taskManager.getAllSubtasksByEpic(epic).contains(subtask), "Подзадача должна быть связана с эпиком.");
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description", Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.contains(task1), "Список задач должен содержать task1.");
        assertTrue(tasks.contains(task2), "Список задач должен содержать task2.");
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description", Duration.ZERO, null);
        Epic epic2 = new Epic("Epic 2", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);

        List<Epic> epics = taskManager.getAllEpics();
        assertTrue(epics.contains(epic1), "Список эпиков должен содержать epic1.");
        assertTrue(epics.contains(epic2), "Список эпиков должен содержать epic2.");
    }

    @Test
    void testGetAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), epic.getId());
        taskManager.saveSubtask(subtask1);
        taskManager.saveSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(subtasks.contains(subtask1), "Список подзадач должен содержать subtask1.");
        assertTrue(subtasks.contains(subtask2), "Список подзадач должен содержать subtask2.");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.saveTask(task);

        task.setName("Updated Task 1");
        taskManager.updateTask(task);

        assertEquals("Updated Task 1", taskManager.getTaskById(task.getId()).getName(), "Имя задачи должно обновляться.");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        epic.setName("Updated Epic 1");
        taskManager.updateEpic(epic);

        assertEquals("Updated Epic 1", taskManager.getEpicById(epic.getId()).getName(), "Имя эпика должно обновляться.");
    }

    @Test
    void testUpdateSubtask() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
        taskManager.saveSubtask(subtask);

        subtask.setName("Updated Subtask 1");
        taskManager.updateSubtask(subtask);

        assertEquals("Updated Subtask 1", taskManager.getSubtaskById(subtask.getId()).getName(), "Имя подзадачи должно обновляться.");
    }

    @Test
    void testDeleteTaskById() {
        Task task = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.saveTask(task);

        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()), "Задача должна быть удалена.");
    }

    @Test
    void testDeleteEpicById() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()), "Эпик должен быть удалён.");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Связанные подзадачи должны быть удалены.");
    }

    @Test
    void testDeleteSubtaskById() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
        taskManager.saveSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача должна быть удалена.");
    }

    @Test
    void testDeleteAllTasks() {
        Task task1 = new Task("Task 1", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description", Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены.");
    }

    @Test
    void testDeleteAllEpics() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Все эпики должны быть удалены.");
    }

    @Test
    void testDeleteAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Description", Duration.ZERO, null);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
        taskManager.saveSubtask(subtask);

        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все подзадачи должны быть удалены.");
    }
}