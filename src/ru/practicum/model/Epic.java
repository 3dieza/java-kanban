package ru.practicum.model;

import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtaskList;

    public Epic(String name, String description, Status status, List<Subtask> subtasks) {
        super(name, description, status);
        this.subtaskList = subtasks;
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
    }

    public List<Subtask> getSubtask() {
        return subtaskList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskList=" + subtaskList +
                '}';
    }
}
