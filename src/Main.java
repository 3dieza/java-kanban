import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.service.TaskManager;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task = new Task("Переезд", "Описание таски", Status.NEW);
        Task task2 = new Task("Переезд2", "Описание таски", Status.NEW);
        Task task3 = new Task("Переезд3", "Описание таски", Status.NEW);

        taskManager.saveTask(task);
        taskManager.saveTask(task2);
        taskManager.saveTask(task3);
        System.out.println("_".repeat(89) + "\nДобавляем Таски: " + "\n" + task + ";\n" + task2 + ";\n" + task3);

        Epic epic = new Epic("epic", "Подробное описание эпика", Status.NEW, null);
        Epic epic2 = new Epic("epic2", "Подробное описание эпика2", Status.NEW, null);

        taskManager.saveEpic(epic);
        taskManager.saveEpic(epic2);
        System.out.println("\nДобавляем Эпики: " + "\n" + epic + ";\n" + epic2);

        Subtask subtask = new Subtask("subtask", "Составить маршрут", Status.DONE, epic);
        Subtask subtask2 = new Subtask("subtask2", "Посчитать бюджет", Status.DONE, epic2);
        Epic epic4 = new Epic("epic2", "Подробное описание эпика2", Status.NEW, new ArrayList<>(Arrays.asList(subtask, subtask2)));
        System.out.println("+++++++++++++++++++"+epic4);
        taskManager.saveSubtask(subtask);
        taskManager.saveSubtask(subtask2);
        System.out.println("\nДобавляем СабТаски: \n" + subtask + subtask2);

        System.out.println("\nПоказать все Таски: ");
        taskManager.getAllTasks();
        System.out.println("\nПоказать все Эпики: ");
        taskManager.getAllEpics();
        System.out.println("\nПоказать все СабТаски: ");
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("_".repeat(89) + "\nПолучаем Таску по айдишнику: " + task2.getId());
        System.out.println(taskManager.getTaskById(task2));

        System.out.println("\nПолучаем Эпик по айдишнику: " + epic.getId());
        System.out.println(taskManager.getEpicById(epic.getId()));

        System.out.println("\nПолучаем СабТаску по айдишнику: " + subtask2.getId());
        System.out.println(taskManager.getSubtaskById(subtask2.getId()));

        System.out.println("_".repeat(89) + "\nОбновляем таску по айди: " + task2.getId());
        Task updatedTask = taskManager.updateTask(new Task("Переезд_изменен",
                "Описание таски", Status.DONE), task2.getId());
        System.out.println("Изменяемый объект: " + updatedTask + "\nобновлен на " +
                taskManager.getTaskById(updatedTask));

        System.out.println("\nПоказать все Таски: ");
        taskManager.getAllTasks();

        System.out.println("\nОбновляем сабТаску по айди: " + subtask.getId());
        taskManager.updateSubtask(new Subtask("subtask_изменен", "Составить маршрут",
                Status.NEW, epic), subtask.getId());
        System.out.println("\nПоказать все СабТаски: ");
        System.out.println(taskManager.getAllSubtasks());

        System.out.print("\nОбновляем Эпик по айди: " + epic.getId() + "\n");
        taskManager.updateEpic(new Epic("epic_изменен", "Подробное описание эпика",
                Status.NEW, null), epic.getId());
        System.out.println("\nПоказать все Эпики: ");
        taskManager.getAllEpics();

        System.out.println("_".repeat(89) + "\nОбновляем статус Эпика.");
        Epic epic3 = new Epic("epic3", "Подробное описание эпика3",
                Status.NEW, new ArrayList<>(Arrays.asList(subtask, subtask2)));
        taskManager.saveEpic(epic3);
        System.out.println("Эпик до обновления: " + epic3);
        taskManager.updateEpicStatus(epic3, epic3.getSubtask());
        System.out.println("\nЭпик после  обновления: " + epic3);

        System.out.println("_".repeat(89) + "\nПолучаем все сабТаски эпика");
        System.out.println(taskManager.getAllSubtasksByEpic(epic3));

        Subtask subtask4 = new Subtask("subtask4", "Посчитать бюджет", Status.DONE, epic2);
        taskManager.saveSubtask(subtask4);
        System.out.println("_".repeat(89) + "\nУдаляем subtask по айдишнику:" + subtask4.getId());
        System.out.println("\nПоказать все subtasks: " + taskManager.getAllSubtasks());
        taskManager.deleteSubtaskById(subtask4.getId());
        System.out.println("\nПоказать все subtasks после удаления: " + taskManager.getAllSubtasks());


        System.out.println("_".repeat(89) + "\nУдаляем таску по айдишнику:" + task2.getId());
        taskManager.deleteTaskById(task2.getId());
        System.out.println("\nПоказать все Таски после удаления: ");
        taskManager.getAllTasks();

        System.out.println("_".repeat(89) + "\nУдаляем epic по айдишнику:" + epic3.getId());
        taskManager.deleteEpicById(epic3.getId());
        System.out.println("\nПоказать все epic после удаления: ");
        taskManager.getAllEpics();


        System.out.println("_".repeat(89) + "\nУдаляем все epic.");
        taskManager.deleteAllEpics();
        System.out.println("Показать все epic после удаления: ");
        taskManager.getAllEpics();

        System.out.println("\nУдаляем все таски.");
        taskManager.deleteAllTasks();
        System.out.println("Показать все таски после удаления: ");
        taskManager.getAllTasks();

        System.out.println("\nУдаляем все subtasks.");
        taskManager.deleteAllSubtasks();
        System.out.println("Показать все subtasks после удаления: ");
        System.out.println(taskManager.getAllSubtasks());


    }
}
