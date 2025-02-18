package ru.practicum.service;

import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final HistoryManager historyManager;

    private final TreeSet<Epic> priorityEpics = new TreeSet<>(Comparator.comparing(
            (Epic epic) -> epic.getStartTime() != null ? epic.getStartTime() : LocalDateTime.MIN
    ).thenComparing(Epic::getId));

    private final TreeSet<Task> priorityTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return Integer.compare(task1.getId(), task2.getId());
        }
        if (task1.getStartTime() == null) return 1;
        if (task2.getStartTime() == null) return -1;
        int compareByTime = task1.getStartTime().compareTo(task2.getStartTime());
        return compareByTime != 0 ? compareByTime : Integer.compare(task1.getId(), task2.getId());
    });

    private final TreeSet<Subtask> prioritySubtasks = new TreeSet<>((subtask1, subtask2) -> {
        if (subtask1.getStartTime() == null && subtask2.getStartTime() == null) {
            return Integer.compare(subtask1.getId(), subtask2.getId());
        }
        if (subtask1.getStartTime() == null) return 1;
        if (subtask2.getStartTime() == null) return -1;
        int compareByTime = subtask1.getStartTime().compareTo(subtask2.getStartTime());
        return compareByTime != 0 ? compareByTime : Integer.compare(subtask1.getId(), subtask2.getId());
    });

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void saveTask(Task task) {
        validateTask(task);
        task.setId(idCounter++);
        if (task.getStartTime() != null) {
            priorityTasks.add(task);
        }
    }

    @Override
    public void saveEpic(Epic epic) {
        epic.setId(idCounter++);
        updateEpicTime(epic);

        priorityEpics.add(epic);
        System.out.println("Добавлен эпик в priorityEpics: " + epic.getId());
    }

    @Override
    public void saveSubtask(Subtask subtask) {
        validateTask(subtask);
        Epic epic = priorityEpics.stream()
                .filter(e -> Objects.equals(e.getId(), subtask.getEpicId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Эпик с ID " + subtask.getEpicId() + " не найден."));
        subtask.setId(idCounter++);
        if (subtask.getStartTime() != null) {
            prioritySubtasks.add(subtask);
        }
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = priorityTasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = priorityEpics.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        if (epic != null) {
            historyManager.add(new Epic(epic));
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = prioritySubtasks.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        if (subtask != null) {
            historyManager.add(new Subtask(subtask));
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || task.getId() == null) {
            throw new IllegalArgumentException("Task is null or ID is not set");
        }

        // Ищем задачу в TreeSet по ID
        Task existingTask = priorityTasks.stream()
                .filter(t -> Objects.equals(t.getId(), task.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + task.getId() + " does not exist"));

        // Удаляем старую версию задачи из TreeSet
        priorityTasks.remove(existingTask);

        // Обновляем поля задачи
        existingTask.setName(task.getName());
        existingTask.setDescription(task.getDescription());
        existingTask.setStartTime(task.getStartTime());
        existingTask.setDuration(task.getDuration());

        // Добавляем обновленную задачу обратно в TreeSet
        priorityTasks.add(existingTask);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || epic.getId() == null) {
            throw new IllegalArgumentException("Epic is null or ID is not set");
        }

        Epic existingEpic = priorityEpics.stream()
                .filter(e -> Objects.equals(e.getId(), epic.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Epic with ID " + epic.getId() + " does not exist"));

        priorityEpics.remove(existingEpic);

        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        existingEpic.setStartTime(epic.getStartTime());
        existingEpic.setDuration(epic.getDuration());

        priorityEpics.add(existingEpic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        prioritySubtasks.remove(subtask);
        validateTask(subtask);
        prioritySubtasks.add(subtask);

        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        // Находим задачу в TreeSet
        Task taskToDelete = priorityTasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);

        if (taskToDelete != null) {
            // Удаляем точный объект из TreeSet
            boolean removed = priorityTasks.remove(taskToDelete);

            // Проверяем, удалось ли удалить задачу
            if (!removed) {
                throw new IllegalStateException("Failed to remove task with ID " + id + " from TreeSet");
            }

            // Удаляем задачу из истории
            historyManager.remove(id);
        } else {
            throw new IllegalArgumentException("Task with ID " + id + " does not exist");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                deleteSubtaskById(subtask.getId());
            }
            priorityEpics.remove(epic);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            prioritySubtasks.remove(subtask);
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(priorityTasks);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(priorityEpics);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(prioritySubtasks);
    }

    @Override
    public List<Subtask> getAllSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        priorityTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : priorityEpics) {
            for (Subtask subtask : epic.getSubtasks()) {
                prioritySubtasks.remove(subtask);
            }
        }
        priorityEpics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : priorityEpics) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        prioritySubtasks.clear();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();
        if (subtasks == null || subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean allNew = subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    void updateEpicTime(Epic epic) {
        if (epic.getSubtasks() == null) {
            epic.setSubtasks(new ArrayList<>()); // Инициализация пустого списка
        }

        List<Subtask> subtasks = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }

        LocalDateTime startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(startTime);
        epic.setDuration(totalDuration);
    }

    private boolean isOverlapping(Task newTask, Task existingTask) {
        if (newTask.getStartTime() == null || existingTask.getStartTime() == null) {
            return false;
        }
        LocalDateTime newTaskStart = newTask.getStartTime();
        LocalDateTime newTaskEnd = newTaskStart.plus(newTask.getDuration());
        LocalDateTime existingTaskStart = existingTask.getStartTime();
        LocalDateTime existingTaskEnd = existingTaskStart.plus(existingTask.getDuration());
        return newTaskStart.isBefore(existingTaskEnd) && existingTaskStart.isBefore(newTaskEnd);
    }

    private void validateTask(Task newTask) {
        for (Task existingTask : getPrioritizedTasks()) {
            // Пропускаем проверку самой задачи
            if (Objects.equals(existingTask.getId(), newTask.getId())) {
                continue;
            }

            // Проверка пересечения для подзадач
            if (newTask instanceof Subtask && existingTask instanceof Subtask) {
                if (isOverlapping(newTask, existingTask)) {
                    throw new IllegalArgumentException("Подзадача пересекается с другой подзадачей: " + existingTask);
                }
            }

            // Проверка пересечения для задач
            if (!(newTask instanceof Epic || newTask instanceof Subtask) && !(existingTask instanceof Epic || existingTask instanceof Subtask)) {
                if (isOverlapping(newTask, existingTask)) {
                    throw new IllegalArgumentException("Задача пересекается с другой задачей: " + existingTask);
                }
            }

            // Проверка пересечения для эпиков
            if (newTask instanceof Epic && existingTask instanceof Epic) {
                if (isOverlapping(newTask, existingTask)) {
                    throw new IllegalArgumentException("Эпик пересекается с другим эпиком: " + existingTask);
                }
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedTasks = new ArrayList<>();

        // Объединяем задачи и подзадачи в один список
        prioritizedTasks.addAll(priorityTasks);
        prioritizedTasks.addAll(prioritySubtasks);

        // Сортируем по `startTime`, при этом `null`-значения идут в конец
        prioritizedTasks.sort(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

        return prioritizedTasks;
    }
}