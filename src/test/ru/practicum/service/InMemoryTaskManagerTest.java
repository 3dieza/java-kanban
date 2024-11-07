package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void testTasksEqualityById() {
        Task task1 = new Task("Задача", "Описание");
        Task task2 = new Task("Задача", "Описание");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void testSubtasksEqualityById() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.saveEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача", "Описание", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача", "Описание", epic.getId());
        subtask1.setSubtaskId(1);
        subtask2.setSubtaskId(1);
        assertEquals(subtask1, subtask2,
                "Подзадачи с одинаковым ID должны быть равны");
    }

    @Test
    void testPreventEpicSelfSubtask() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        subtask.setSubtaskId(epic.getId());
        taskManager.saveSubtask(subtask);

        assertNotEquals(epic.getId(), subtask.getSubtaskId(),
                "Эпик не должен быть подзадачей самого себя");
    }

    @Test
    void testPreventSubtaskBeingItsOwnEpic() {
        Subtask subtask = new Subtask("Подзадача", "Описание", 1);
        subtask.setId(1);  // Устанавливаем ID подзадачи

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            subtask.setEpicId(1);  // Попытка установить epicId равным ID подзадачи
        });

        assertEquals("Подзадача не может ссылаться на свой же эпик.", thrown.getMessage());
    }

    @Test
    void testGetDefaultManagerInstances() {
        TaskManager manager = Managers.getDefaultTaskManager();
        assertNotNull(manager, "TaskManager должен быть проинициализирован");
        assertTrue(manager instanceof InMemoryTaskManager,
                "TaskManager должен быть экземпляром InMemoryTaskManager");

        HistoryManager history = Managers.getDefaultHistoryManager();
        assertNotNull(history, "HistoryManager должен быть проинициализирован");
        assertTrue(history instanceof InMemoryHistoryManager,
                "HistoryManager должен быть экземпляром InMemoryHistoryManager");
    }

    @Test
    void testSaveAndRetrieveTasksById() {
        Task task = new Task("Задача", "Описание");
        taskManager.saveTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask, "Задача должна быть доступна по ID");
    }

    @Test
    void testGeneratedAndSpecifiedIdTasksConflict() {
        Task generatedTask = new Task("Генерируемая задача", "Описание");
        taskManager.saveTask(generatedTask);

        Task specifiedTask = new Task("Задача с ID", "Описание");
        specifiedTask.setId(generatedTask.getId());
        taskManager.saveTask(specifiedTask);

        assertNotEquals(generatedTask, specifiedTask,
                "Задачи с разными ID не должны конфликтовать");
    }

    @Test
    void testTaskFieldsImmutability() {
        Task task = new Task("Исходная задача", "Описание");
        taskManager.saveTask(task);

        Task storedTask = taskManager.getTaskById(task.getId());
        storedTask.setName("Измененное имя");

        Task reFetchedTask = taskManager.getTaskById(task.getId());
        assertEquals("Исходная задача", reFetchedTask.getName(),
                "Имя задачи должно оставаться неизменным после изменения в другой ссылке");
    }

    @Test
    void testHistoryAddAndPreserveOriginalTaskData() {
        Task task = new Task("Задача в истории", "Описание");
        taskManager.saveTask(task);

        taskManager.getTaskById(task.getId());
        task.setDescription("Новое описание");

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals("Описание", history.get(0).getDescription(),
                "Оригинальное описание задачи должно сохраниться в истории");
    }

    @Test
    void testAddNewTask() {
        Task task = new Task("Тестовая задача", "Описание задачи");
        taskManager.saveTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Сохраненная и полученная задачи должны совпадать");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void testHistoryAddFunctionality() {
        Task task = new Task("Задача для истории", "Описание");
        taskManager.saveTask(task);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История должна быть не пустой");
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной");
    }
}