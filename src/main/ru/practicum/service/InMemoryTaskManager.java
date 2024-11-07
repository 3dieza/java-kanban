package ru.practicum.service;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс InMemoryTaskManager предоставляет методы для управления задачами, эпиками и подзадачами в памяти.
 * Реализует интерфейс TaskManager и поддерживает историю просмотров с помощью HistoryManager.
 */
public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    /**
     * Конструктор инициализирует новый InMemoryTaskManager с заданным менеджером истории.
     *
     * @param historyManager Менеджер истории для отслеживания просмотра задач, эпиков и подзадач
     */
    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void saveTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void saveEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void saveSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setSubtaskId(idCounter++);
            subtasks.put(subtask.getSubtaskId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(new Task(task));
        }
        return task != null ? new Task(task) : null;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(new Epic(epic));
        }
        return epic != null ? new Epic(epic) : null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(new Subtask(subtask));
        }
        return subtask != null ? new Subtask(subtask) : null;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getSubtaskId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getSubtaskId());
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allDone = subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean anyInProgress = subtasks.stream().anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}