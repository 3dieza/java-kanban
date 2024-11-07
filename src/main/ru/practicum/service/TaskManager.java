package ru.practicum.service;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import java.util.List;

/**
 * Интерфейс TaskManager предоставляет методы для управления задачами, эпиками и подзадачами.
 * Позволяет сохранять, получать, обновлять и удалять задачи, а также управлять историей просмотров.
 */
public interface TaskManager {
    void saveTask(Task task);
    void saveEpic(Epic epic);
    void saveSubtask(Subtask subtask);

    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);

    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();
    List<Subtask> getAllSubtasksByEpic(Epic epic);

    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();

    List<Task> getHistory();
}