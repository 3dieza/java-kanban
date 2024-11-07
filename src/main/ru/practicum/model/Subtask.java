package ru.practicum.model;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;
    private int subtaskId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        setEpicId(epicId);
    }

    /**
     * Копирующий конструктор для создания нового экземпляра Subtask на основе другого Subtask.
     * Копирует все данные задачи и сохраняет идентификатор связанного эпика.
     *
     * @param other другой объект Subtask, данные которого нужно скопировать
     */
    public Subtask(Subtask other) {
        super(other);
        this.epicId = other.epicId;
    }

    public Subtask(int subtaskId, String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.subtaskId = subtaskId;
    }

    public int getEpicId() {
        return epicId;
    }

    public int getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(int subtaskId) {
        this.subtaskId = subtaskId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            throw new IllegalArgumentException("Подзадача не может ссылаться на свой же эпик.");
        }
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{subtaskId=" + getId() +
                ", name=" + getName() +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return getEpicId() == subtask.getEpicId() && getSubtaskId() == subtask.getSubtaskId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId(), getSubtaskId());
    }
}
