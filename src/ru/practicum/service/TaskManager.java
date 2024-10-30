package ru.practicum.service;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int idCounter = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public List<Subtask> getAllSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    public void getAllTasks() {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                System.out.println(task);
            }
        } else {
            System.out.println("No tasks found");
        }
    }

    public List<Epic> getAllEpics() {
        return epics.isEmpty() ? null : new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Список сабТасок пуст!");
        }
        return new ArrayList<>(subtasks.values());
    }

    public void saveTask(Task task) {
        task.setId(idCounter);
        tasks.put(idCounter++, task);
    }

    public void saveEpic(Epic epic) {
        epic.setId(idCounter);
        epics.put(idCounter++, epic);
    }

    public void saveSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setSubtaskId(idCounter);
            subtasks.put(idCounter++, subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        } else {
            System.out.println("Нет такого эпика");
        }
    }

    public Task getTaskById(Integer id) {
        return tasks.getOrDefault(id, null);
    }

    public Epic getEpicById(int idCounter) {
        return epics.get(idCounter);
    }

    public Subtask getSubtaskById(int idCounter) {
        return subtasks.get(idCounter);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getSubtaskId())) {
            subtasks.put(subtask.getSubtaskId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic);
            }
        }
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpicById(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getSubtaskId());
            }
        }
    }

    public void updateEpicStatus(Epic epic) {
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
}