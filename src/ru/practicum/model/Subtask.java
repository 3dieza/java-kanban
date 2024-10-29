package ru.practicum.model;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;
    private int subtaskId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
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

    @Override
    public String toString() {
        return "Subtask{" +
                "subtaskId=" + subtaskId + "," +
                "name=" + (getName() != null ? getName() : "Unnamed") + "," +
                "description='" + (getDescription() != null ? getDescription() : "No description") + ", " +
                "epicId=" + (epicId != 0 ? epicId : "No epic") + ", " +
                "status=" + getStatus() +
                '}' + "\n";
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
