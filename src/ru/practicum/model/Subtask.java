package ru.practicum.model;

public class Subtask extends Task {
    private final Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + (getName() != null ? getName() : "Unnamed") + "'," +
                "description='" + (getDescription() != null ? getDescription() : "No description") + "', " +
                "status=" + (getStatus() != null ? getStatus() : "No status") + ", " +
                "taskId=" + taskId + ", " +
                "epicId=" + (epic != null && epic.getId() != 0 ? epic.getId() : "No epic") +
                '}' + "\n";
    }
}
