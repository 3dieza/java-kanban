package ru.practicum.service;

import ru.practicum.exception.ManagerSaveException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    /**
     * Конструктор инициализирует новый InMemoryTaskManager с заданным менеджером истории.
     *
     * @param historyManager Менеджер истории для отслеживания просмотра задач, эпиков и подзадач
     */
    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
            }
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%d,SUBTASK,%s,%s,%s,%d\n", subtask.getSubtaskId(), subtask.getName(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic epic) {
            return String.format("%d,EPIC,%s,%s,%s\n", epic.getEpicId(), epic.getName(),
                    epic.getStatus(), epic.getDescription());
        }

        return String.format("%d,TASK,%s,%s,%s\n", task.getId(),task.getName(),task.getStatus(),task.getDescription());
    }


    static FileBackedTaskManager loadFromFile(File file) {
        return null;
        //TODO реализовать
    }

    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        save();
    }

    @Override
    public void saveEpic(Epic epic) {
        super.saveEpic(epic);
        save();
    }

    @Override
    public void saveSubtask(Subtask subtask) {
        super.saveSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
