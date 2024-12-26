package ru.practicum.service;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    /**
     * Конструктор инициализирует новый InMemoryTaskManager с заданным менеджером истории.
     *
     * @param historyManager Менеджер истории для отслеживания просмотра задач, эпиков и подзадач
     */
    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    private void save() {
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
