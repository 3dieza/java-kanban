package ru.practicum.model;

import java.util.Objects;

public class Task {
    private String name;
    private int taskId;

    private Status status;
    private String description;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        setStatus(Status.NEW);
    }

    public Task(int taskId, String name, String description) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        setStatus(Status.NEW);
    }

    // Копирующий конструктор
    public Task(Task other) {
        this.taskId = other.taskId;
        this.name = other.name;
        this.description = other.description;
        this.status = other.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return taskId;
    }

    public void setId(int id) {
        this.taskId = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" + "name='" + name + '\'' + ", description='" + description +
                '\'' + ", id=" + taskId + ", status='" + status + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId);
    }
}
