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

    HashMap<Integer, Task> taskHashMap = new HashMap<>();
    HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();

    public void getAllTasks() {
        if (!taskHashMap.isEmpty()) {
            for (Task task : taskHashMap.values()) {
                System.out.println(task);
            }
        } else {
            System.out.println("No tasks found");
        }
    }

    public void getAllEpics() {
        if (!epicHashMap.isEmpty()) {
            for (Epic epic : epicHashMap.values()) {
                System.out.println(epic);
            }
        } else {
            System.out.println("No epic found");
        }
    }

    public ArrayList<Subtask> getAllSubtasks() {
        if (subtaskHashMap.isEmpty()) {
            System.out.println("Список сабТасок пуст!");
        }
        return new ArrayList<>(subtaskHashMap.values());
    }

    public void saveTask(Task task) {
        taskHashMap.put(idCounter, task);
        task.setId(idCounter++);
    }

    public void saveEpic(Epic epic) {
        epicHashMap.put(idCounter, epic);
        epic.setId(idCounter++);
    }

    public void saveSubtask(Subtask subtask) {
        subtaskHashMap.put(idCounter, subtask);
        subtask.setId(idCounter++);
    }

    public Task getTaskById(Task task) {
        return taskHashMap.get(task.getId());
    }

    public Epic getEpicById(int idCounter) {
        return epicHashMap.get(idCounter);
    }

    public Subtask getSubtaskById(int idCounter) {
        return subtaskHashMap.get(idCounter);
    }

    public Task updateTask(Task task, int idCounter) {
        if (taskHashMap.containsKey(idCounter)) {
            task.setId(idCounter);
            return taskHashMap.put(idCounter, task);
        }
        return null;
    }

    public void updateSubtask(Subtask subtask, int idCounter) {
        if (subtaskHashMap.containsKey(idCounter)) {
            subtask.setId(idCounter);
            subtaskHashMap.put(idCounter, subtask);
        }

    }

    public void updateEpic(Epic epic, int idCounter) {
        if (epicHashMap.containsKey(idCounter)) {
            epic.setId(idCounter);
            epicHashMap.put(idCounter, epic);
        }
    }

    public void deleteAllTasks() {
        taskHashMap.clear();
    }

    public void deleteAllEpics() {
        epicHashMap.clear();
        deleteAllSubtasks();
    }

    public void deleteAllSubtasks() {
        subtaskHashMap.clear();
    }

    public void deleteSubtaskById(int idCounter) {
        subtaskHashMap.remove(idCounter);
    }

    public void deleteTaskById(int idCounter) {
        taskHashMap.remove(idCounter);
    }

    public void deleteEpicById(int idCounter) {
        Epic epic = epicHashMap.get(idCounter);
        List<Subtask> allSubtasksByEpic = getAllSubtasksByEpic(epic);
        allSubtasksByEpic.clear();
        epicHashMap.remove(idCounter);
    }

    public void updateEpicStatus(Epic epic, List<Subtask> subtasks) {
        if (!epicHashMap.containsKey(epic.getId()) || subtasks == null || subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        epic.setStatus(
                subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE) ? Status.DONE :
                        subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW) ? Status.NEW :
                                Status.IN_PROGRESS
        );
    }

    public List<Subtask> getAllSubtasksByEpic(Epic epic) {
        return epic.getSubtask();
    }
}
