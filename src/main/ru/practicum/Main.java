package ru.practicum;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.service.FileBackedTaskManager;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        // Тестирование FileBackedTaskManager
        File tempFile = new File("tasks_export.csv");
        FileBackedTaskManager fileManager = new FileBackedTaskManager(tempFile);
        testFileBackedTaskManager(fileManager, tempFile);

        // Тестирование InMemoryTaskManager
        TaskManager inMemoryManager = Managers.getDefaultTaskManager();
        testInMemoryTaskManager(inMemoryManager);

        // Тестирование пересечений задач, эпиков и подзадач
        testTaskSubtaskEpicOverlaps(inMemoryManager);
        testTaskSubtaskEpicOverlaps(fileManager);

        // Список задач, эпиков и подзадач (по приоритету)
        System.out.println("************************************");
        System.out.println("Список задач, эпиков и подзадач (по приоритету):");
        inMemoryManager.getPrioritizedTasks().forEach(System.out::println);

        // Тестирование обновления задач и эпиков
        testUpdateFunctions(inMemoryManager);
    }

    private static void testFileBackedTaskManager(FileBackedTaskManager fileManager, File tempFile) {
        System.out.println("************************************");
        System.out.println("Тестирование FileBackedTaskManager");

        createAndSaveTasks(fileManager);
        createAndSaveEpics(fileManager);
        createAndSaveSubtasks(fileManager);

        // Проверка загрузки из файла
        System.out.println("Задачи сохранены в файл. Проверка загрузки из файла...");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        loadedManager.getAllTasks().forEach(System.out::println);
        loadedManager.getAllEpics().forEach(System.out::println);
        loadedManager.getAllSubtasks().forEach(System.out::println);

        // Проверка изменения статуса эпика
        testEpicStatusChanges(loadedManager);

        // Проверка истории
        testHistory(loadedManager);

        // Проверка удаления
        testDeletion(loadedManager);
    }

    private static void testInMemoryTaskManager(TaskManager taskManager) {
        System.out.println("************************************");
        System.out.println("Тестирование InMemoryTaskManager");

        createAndSaveTasks(taskManager);
        createAndSaveEpics(taskManager);
        createAndSaveSubtasks(taskManager);

        // Проверка изменения статуса эпика
        testEpicStatusChanges(taskManager);

        // Проверка истории
        testHistory(taskManager);

        // Проверка удаления
        testDeletion(taskManager);
    }

    private static void createAndSaveTasks(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Создание задач...");

        Task task1 = new Task("Задача 1", "Описание задачи 1",
                Duration.ofMinutes(30), LocalDateTime.now().plusYears(1));
        Task task2 = new Task("Задача 2", "Описание задачи 2",
                Duration.ofMinutes(40), LocalDateTime.now());
        manager.saveTask(task1);
        manager.saveTask(task2);

        System.out.println("Созданы задачи: ");
        manager.getAllTasks().forEach(System.out::println);
    }

    private static void createAndSaveEpics(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Создание эпиков...");

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1",
                Duration.ZERO, LocalDateTime.now().plusYears(2));
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2",
                Duration.ZERO, LocalDateTime.now().plusYears(1));
        manager.saveEpic(epic1);
        manager.saveEpic(epic2);

        System.out.println("Созданы эпики: ");
        manager.getAllEpics().forEach(System.out::println);
    }

    private static void createAndSaveSubtasks(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Создание подзадач...");

        Epic epic = manager.getAllEpics().getFirst();
        if (epic != null) {
            Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1",
                    Duration.ofMinutes(20),
                    LocalDateTime.now().plusYears(9), epic.getId());
            Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2",
                    Duration.ofMinutes(50),
                    LocalDateTime.now().plusYears(3), epic.getId());
            manager.saveSubtask(subtask1);
            manager.saveSubtask(subtask2);

            System.out.println("Созданы подзадачи: ");
            manager.getAllSubtasks().forEach(System.out::println);
        }
    }

    private static void testEpicStatusChanges(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Тестирование изменения статуса эпика");

        Epic epic = manager.getAllEpics().getFirst();
        if (epic != null) {
            System.out.println("Начальный статус эпика: " + epic.getStatus());

            Subtask subtask1 = manager.getAllSubtasksByEpic(epic).get(0);
            Subtask subtask2 = manager.getAllSubtasksByEpic(epic).get(1);

            subtask1.setStatus(Status.IN_PROGRESS);
            manager.updateSubtask(subtask1);
            System.out.println("Статус эпика после изменения первой подзадачи: " + epic.getStatus());

            subtask1.setStatus(Status.DONE);
            subtask2.setStatus(Status.DONE);
            manager.updateSubtask(subtask1);
            manager.updateSubtask(subtask2);
            System.out.println("Статус эпика после завершения всех подзадач: " + epic.getStatus());
        }
    }

    private static void testHistory(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Тестирование истории просмотров");

        manager.getAllTasks().forEach(task -> manager.getTaskById(task.getId()));
        manager.getAllEpics().forEach(epic -> manager.getEpicById(epic.getId()));

        System.out.println("История просмотров:");
        manager.getHistory().forEach(System.out::println);
    }

    private static void testDeletion(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Тестирование удаления задач, эпиков и подзадач");

        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

        System.out.println("Все задачи удалены.");
        System.out.println("Текущие задачи: " + manager.getAllTasks());
        System.out.println("Текущие эпики: " + manager.getAllEpics());
        System.out.println("Текущие подзадачи: " + manager.getAllSubtasks());
    }

    private static void testTaskSubtaskEpicOverlaps(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Тестирование пересечений задач, подзадач и эпиков");

        // Создание и сохранение эпиков
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1",
                Duration.ZERO, LocalDateTime.of(2025, 1, 1, 10, 0));
        manager.saveEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2",
                Duration.ZERO, LocalDateTime.of(2025, 1, 1, 12, 0));
        manager.saveEpic(epic2);

        // Добавление подзадач
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1",
                Duration.ofHours(2), LocalDateTime.of(2025, 1, 1, 10, 0), epic1.getId());
        manager.saveSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2",
                Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 11, 0), epic1.getId());
        try {
            manager.saveSubtask(subtask2); // Это должно вызвать исключение
            System.out.println("Добавлена подзадача 2: " + subtask2);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка при добавлении подзадачи 2: " + e.getMessage());
        }

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3",
                Duration.ofHours(2), LocalDateTime.of(2025, 1, 1, 12, 0), epic2.getId());
        manager.saveSubtask(subtask3);

        // Создание и сохранение задач
        Task task1 = new Task("Задача 1", "Описание задачи 1",
                Duration.ofHours(3), LocalDateTime.of(2025, 1, 1, 8, 0));
        manager.saveTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2",
                Duration.ofHours(2), LocalDateTime.of(2025, 1, 1, 9, 30));
        try {
            manager.saveTask(task2); // Это должно вызвать исключение
            System.out.println("Добавлена задача 2: " + task2);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка при добавлении задачи 2: " + e.getMessage());
        }

        Task task3 = new Task("Задача 3", "Описание задачи 3",
                Duration.ofHours(2), LocalDateTime.of(2025, 1, 1, 15, 0));
        manager.saveTask(task3);
    }

    private static void testUpdateFunctions(TaskManager manager) {
        System.out.println("************************************");
        System.out.println("Тестирование функций обновления задач и эпиков");

        // Создание и сохранение задачи
        Task task = new Task("Обновляемая задача", "Описание задачи",
                Duration.ofMinutes(60), LocalDateTime.now().plusDays(1));
        manager.saveTask(task);

        System.out.println("Сохраненная задача: " + task);

        // Обновление задачи
        task.setName("Обновленная задача");
        task.setDescription("Обновленное описание задачи");
        manager.updateTask(task);

        System.out.println("Обновленная задача: " + manager.getTaskById(task.getId()));

        // Создание и сохранение эпика
        Epic epic = new Epic("Обновляемый эпик", "Описание эпика",
                Duration.ZERO, LocalDateTime.now().plusDays(5));
        manager.saveEpic(epic);

        System.out.println("Сохраненный эпик: " + epic);

        // Обновление эпика
        epic.setName("Обновленный эпик");
        epic.setDescription("Обновленное описание эпика");
        manager.updateEpic(epic);

        System.out.println("Обновленный эпик: " + manager.getEpicById(epic.getId()));

        System.out.println("************************************");
    }
}