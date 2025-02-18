package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Integer id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    // Конструктор с ID
    public Task(Integer id, String name, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        setStatus(Status.NEW);
        this.startTime = startTime;
        this.duration = duration;
    }

    // Конструктор без ID
    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        setStatus(Status.NEW);
        this.startTime = startTime;
        this.duration = duration;
    }

    /**
     * Копирующий конструктор.
     *
     * @param other другой объект Task, данные которого нужно скопировать
     */
    public Task(Task other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.status = other.status;
        this.duration = other.duration;
        this.startTime = other.startTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}