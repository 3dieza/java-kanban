package ru.practicum;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();

        createAndDisplayTasks(taskManager);
        createAndDisplayEpics(taskManager);
        createAndDisplaySubtasks(taskManager, taskManager.getAllEpics().get(0)); // Пример использования первого эпика

        checkEpicStatusChanges(taskManager, taskManager.getAllEpics().get(0));
        viewAndDisplayHistory(taskManager);
        updateAndDeleteTasks(taskManager);
        updateAndDeleteSubtasks(taskManager, taskManager.getAllSubtasks().get(0));
        updateAndDeleteEpic(taskManager, taskManager.getAllEpics().get(0));
        deleteAndDisplayAll(taskManager);
    }

    private static void createAndDisplayTasks(TaskManager taskManager) {
        System.out.println("\nСоздаем задачи...");
        Task task1 = new Task("Переезд", "Описание задачи 1");
        Task task2 = new Task("Переезд2", "Описание задачи 2");
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);

        System.out.println("Все задачи после добавления:");
        taskManager.getAllTasks().forEach(System.out::println);
    }

    private static void createAndDisplayEpics(TaskManager taskManager) {
        System.out.println("\nСоздаем эпики...");
        Epic epic1 = new Epic("Эпик1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик2", "Описание эпика 2");
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);

        System.out.println("Все эпики после добавления:");
        taskManager.getAllEpics().forEach(System.out::println);
    }

    private static void createAndDisplaySubtasks(TaskManager taskManager, Epic epic) {
        System.out.println("\nСоздаем подзадачи...");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId());

        taskManager.saveSubtask(subtask1);
        taskManager.saveSubtask(subtask2);

        System.out.println("Подзадачи для эпика ID " + epic.getId() + ":");
        taskManager.getAllSubtasksByEpic(epic).forEach(System.out::println);
    }

    private static void checkEpicStatusChanges(TaskManager taskManager, Epic epic) {
        System.out.println("\nПроверка изменения статуса эпика на основе статусов подзадач...");

        // Начальный статус эпика
        System.out.println("Начальный статус эпика: " + epic.getStatus());

        // Получаем подзадачи эпика
        Subtask subtask1 = taskManager.getAllSubtasksByEpic(epic).get(0);
        Subtask subtask2 = taskManager.getAllSubtasksByEpic(epic).get(1);

        // Обновляем первую подзадачу на статус IN_PROGRESS и проверяем статус эпика
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        System.out.println("Статус эпика после обновления первой подзадачи на IN_PROGRESS: " +
                taskManager.getEpicById(epic.getId()).getStatus());

        // Обновляем обе подзадачи на статус DONE и проверяем статус эпика
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        System.out.println("Статус эпика после обновления всех подзадач на DONE: " +
                taskManager.getEpicById(epic.getId()).getStatus());
    }

    private static void viewAndDisplayHistory(TaskManager taskManager) {
        System.out.println("\nПросматриваем задачи и эпики для истории...");
        taskManager.getAllTasks().forEach(task -> taskManager.getTaskById(task.getId()));
        taskManager.getAllEpics().forEach(epic -> taskManager.getEpicById(epic.getId()));

        System.out.println("История просмотров:");
        taskManager.getHistory().forEach(System.out::println);
    }

    private static void updateAndDeleteTasks(TaskManager taskManager) {
        System.out.println("\nОбновляем задачу и удаляем одну задачу...");

        Task taskToUpdate = taskManager.getAllTasks().get(1); // Пример использования второй задачи
        taskToUpdate.setDescription("Обновленное описание задачи 2");
        taskManager.updateTask(taskToUpdate);

        System.out.println("Все задачи после обновления:");
        taskManager.getAllTasks().forEach(System.out::println);

        Task taskToDelete = taskManager.getAllTasks().get(0); // Пример использования первой задачи
        taskManager.deleteTaskById(taskToDelete.getId());

        System.out.println("Все задачи после удаления:");
        taskManager.getAllTasks().forEach(System.out::println);
    }

    private static void updateAndDeleteSubtasks(TaskManager taskManager, Subtask subtask) {
        System.out.println("\nОбновляем и удаляем подзадачу...");

        subtask.setDescription("Обновленное описание подзадачи 1");
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);

        System.out.println("Подзадачи после обновления:");
        taskManager.getAllSubtasks().forEach(System.out::println);

        taskManager.deleteSubtaskById(subtask.getSubtaskId());
        System.out.println("Подзадачи после удаления:");
        taskManager.getAllSubtasks().forEach(System.out::println);
    }

    private static void updateAndDeleteEpic(TaskManager taskManager, Epic epic) {
        System.out.println("\nОбновляем и удаляем эпик...");

        epic.setDescription("Обновленное описание эпика 1");
        taskManager.updateEpic(epic);

        System.out.println("Все эпики после обновления:");
        taskManager.getAllEpics().forEach(System.out::println);

        // Получение подзадачи для проверки метода getSubtaskById
        if (!taskManager.getAllSubtasks().isEmpty()) {
            Subtask subtask = taskManager.getAllSubtasks().get(0);
            System.out.println("Получение подзадачи по ID: " + subtask.getSubtaskId());
            System.out.println(taskManager.getSubtaskById(subtask.getSubtaskId()));
        }

        taskManager.deleteEpicById(epic.getId());
        System.out.println("Все эпики после удаления:");
        taskManager.getAllEpics().forEach(System.out::println);
    }

    private static void deleteAndDisplayAll(TaskManager taskManager) {
        System.out.println("\nУдаляем все задачи, эпики и подзадачи...");
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        System.out.println("Все задачи после удаления всех:");
        taskManager.getAllTasks().forEach(System.out::println);

        System.out.println("Все эпики после удаления всех:");
        taskManager.getAllEpics().forEach(System.out::println);

        System.out.println("Все подзадачи после удаления всех:");
        taskManager.getAllSubtasks().forEach(System.out::println);
    }
}