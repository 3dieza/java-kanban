package ru.practicum.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        setStatus(Status.NEW);
    }

    /**
     * Копирующий конструктор для создания нового экземпляра Epic на основе другого Epic.
     * Копирует все данные задачи и создает отдельную копию списка подзадач.
     *
     * @param other другой объект Epic, данные которого нужно скопировать
     */
    public Epic(Epic other) {
        super(other);
        epicId = other.epicId;
        this.subtasks = new ArrayList<>(other.subtasks);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicId = " + getEpicId() + ", " +
                "subtasks = " + getSubtasks() + ", " +
                "name = " + getName() + ", " +
                "description = " + getDescription() + ", " +
                "status = " + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubtasks(), epic.getSubtasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtasks());
    }
}
