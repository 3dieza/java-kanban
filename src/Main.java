
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("\n" + "=".repeat(20) + " TASK TESTING " + "=".repeat(20) + "\n");
        testTasks(taskManager);

        System.out.println("\n" + "=".repeat(20) + " EPIC TESTING " + "=".repeat(20) + "\n");
        testEpics(taskManager);

        System.out.println("\n" + "=".repeat(20) + " SUBTASK & EPIC STATUS TESTING " + "=".repeat(20) + "\n");
        testSubtasksAndEpicStatus(taskManager);
    }

    private static void testTasks(TaskManager taskManager) {
        System.out.println("Создаем задачи...");
        Task task1 = new Task("Переезд", "Описание задачи 1");
        Task task2 = new Task("Переезд2", "Описание задачи 2");
        Task task3 = new Task("Переезд3", "Описание задачи 3");

        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveTask(task3);

        System.out.println("Все задачи после добавления:");
        taskManager.getAllTasks();

        System.out.println("\nОбновляем задачу с ID 2:");
        task2.setName("Переезд2_Обновленный");
        taskManager.updateTask(task2);
        taskManager.getAllTasks();

        System.out.println("\nПолучение задачи по ID 2:");
        System.out.println(taskManager.getTaskById(2));

        System.out.println("\nУдаляем задачу с ID 1:");
        taskManager.deleteTaskById(task1.getId());
        taskManager.getAllTasks();

        System.out.println("\nУдаляем все задачи:");
        taskManager.deleteAllTasks();
        taskManager.getAllTasks();
    }

    private static void testEpics(TaskManager taskManager) {
        System.out.println("Создаем эпики...");
        Epic epic1 = new Epic("Эпик1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик2", "Описание эпика 2");

        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);

        System.out.println("Все эпики после добавления:");
        System.out.println(taskManager.getAllEpics());

        System.out.println("\nПолучение эпика по ID " + epic1.getId());
        System.out.println(taskManager.getEpicById(epic1.getId()));

        System.out.println("\nОбновляем эпик с ID " + epic1.getId());
        epic1.setName("Эпик1_Обновленный");
        taskManager.updateEpic(epic1);
        System.out.println(taskManager.getAllEpics());

        System.out.println("\nУдаляем эпик с ID " + epic2.getId());
        taskManager.deleteEpicById(epic2.getId());
        System.out.println(taskManager.getAllEpics());

        System.out.println("\nУдаляем все эпики:");
        taskManager.deleteAllEpics();
        System.out.println(taskManager.getAllEpics());
    }

    private static void testSubtasksAndEpicStatus(TaskManager taskManager) {
        System.out.println("Создаем эпик и подзадачи для проверки статусов...");

        // Создаем эпик
        Epic epic = new Epic("Эпик со статусами", "Эпик для проверки статусов подзадач");
        taskManager.saveEpic(epic);

        // Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId());
        taskManager.saveSubtask(subtask1);
        taskManager.saveSubtask(subtask2);

        System.out.println("\nВсе подзадачи эпика (метод getAllSubtasksByEpic):");
        System.out.println(taskManager.getAllSubtasksByEpic(epic));

        System.out.println("\nПолучение подзадачи по ID " + subtask1.getSubtaskId());
        System.out.println(taskManager.getSubtaskById(subtask1.getSubtaskId()));

        // Проверка начального статуса эпика (NEW)
        System.out.println("\nЭпик после добавления подзадач (ожидается статус NEW):");
        System.out.println(epic);

        // Устанавливаем статус IN_PROGRESS для одной из подзадач
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        System.out.println("\nЭпик после изменения статуса одной подзадачи на IN_PROGRESS (ожидается статус IN_PROGRESS):");
        System.out.println(epic);

        // Завершаем все подзадачи и проверяем статус эпика (ожидается DONE)
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        System.out.println("\nЭпик после завершения всех подзадач (ожидается статус DONE):");
        System.out.println(epic);

        // Тестируем удаление подзадачи по ID
        System.out.println("\nУдаление подзадачи с ID " + subtask1.getSubtaskId());
        taskManager.deleteSubtaskById(subtask1.getSubtaskId());
        System.out.println("Подзадачи после удаления одной подзадачи:");
        System.out.println(taskManager.getAllSubtasksByEpic(epic));

        // Тестируем удаление всех подзадач
        System.out.println("\nУдаляем все подзадачи эпика (метод deleteAllSubtasks):");
        taskManager.deleteAllSubtasks();
        System.out.println("Подзадачи после удаления всех подзадач:");
        System.out.println(taskManager.getAllSubtasks());
    }
}