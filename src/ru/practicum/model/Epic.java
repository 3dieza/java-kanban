package ru.practicum.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    /**
     * поле следует сразу проинициализировать пустым листом, оно не должно быть final, и так же следует добавить для него сеттер
     * Так же в названии переменной/поля/параметра и т.д. не стоит использовать ее тип, это противоречит правилам именования.
     * В данном случае можно использовать просто множественное число (subtasks) .
     * Аналогично с подобными названиями по проекту
     * <p>
     * .DONE
     */
    private List<Subtask> subtasks = new ArrayList<>();

    /**
     * параметр status следует убрать из всех конструкторов Task, Epic, Subtasks, статус изначально при создании должен быть New,
     * а далее меняться должен автоматически при выполнении определенных методов класса TaskManager.
     * Так же тут следует убрать параметр List<Subtask> subtasks, Эпик изначально создается без сабтаск,
     * а далее по мере заполнения сабтасками, поле должно обновляться сеттером
     * .DONE
     */
    public Epic(String name, String description) {
        super(name, description);
        setStatus(Status.NEW);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks = " + subtasks + ", " +
                "name = " + getName() + ", " +
                "description = " + getDescription() + ", " +
                "status = " + getStatus() +
                '}';
    }

    /**
     * так как тут добавилось новое поле subtasks нужно переопределить hashCode, equals,
     * потому что они не учитывают это поле
     */
    //не понял - если учитывать subtasks, то т.к список subtasks изменяемый - получаются хеш будет постоянно меняться
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return getId() == epic.getId();
    }
}
