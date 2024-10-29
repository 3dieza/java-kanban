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

    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    public List<Subtask> getAllSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    public void getAllTasks() {
        if (!taskMap.isEmpty()) {
            for (Task task : taskMap.values()) {
                System.out.println(task);
            }
        } else {
            System.out.println("No tasks found");
        }
    }

    public List<Epic> getAllEpics() {
        return epicMap.isEmpty() ? null : new ArrayList<>(epicMap.values());
    }

    public List<Subtask> getAllSubtasks() {
        if (subtaskMap.isEmpty()) {
            System.out.println("Список сабТасок пуст!");
        }
        return new ArrayList<>(subtaskMap.values());
    }

    public void saveTask(Task task) {
        task.setId(idCounter);
        taskMap.put(idCounter++, task);
    }

    public void saveEpic(Epic epic) {
        epic.setId(idCounter);
        epicMap.put(idCounter++, epic);
    }

    public void saveSubtask(Subtask subtask) {
        if (epicMap.containsKey(subtask.getEpicId())) {
            subtask.setSubtaskId(idCounter);
            subtaskMap.put(idCounter++, subtask);

            Epic epic = epicMap.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        } else {
            System.out.println("Нет такого эпика");
        }
    }

    public Task getTaskById(Integer id) {
        return taskMap.getOrDefault(id, null);
    }

    public Epic getEpicById(int idCounter) {
        return epicMap.get(idCounter);
    }

    public Subtask getSubtaskById(int idCounter) {
        return subtaskMap.get(idCounter);
    }

    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskMap.containsKey(subtask.getSubtaskId())) {
            subtaskMap.put(subtask.getSubtaskId(), subtask);
            Epic epic = epicMap.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.put(epic.getId(), epic);
        }
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    public void deleteAllEpics() {
        epicMap.clear();
        subtaskMap.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epicMap.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
        subtaskMap.clear();
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtaskMap.remove(subtaskId);
        if (subtask != null) {
            Epic epic = epicMap.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic);
            }
        }
    }

    public void deleteTaskById(int taskId) {
        taskMap.remove(taskId);
    }

    public void deleteEpicById(int epicId) {
        Epic epic = epicMap.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtaskMap.remove(subtask.getSubtaskId());
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