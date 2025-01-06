package ru.practicum.service;

import ru.practicum.exception.ManagerSaveException;
import ru.practicum.model.Epic;
import ru.practicum.model.Status;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    /**
     * Конструктор инициализирует новый FileBackedTaskManager, который получает файл для автосохранения
     * в своём конструкторе и сохраняет его в поле.
     */
    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
            }
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%d,SUBTASK,%s,%s,%s,%d\n",
                    subtask.getSubtaskId(),
                    subtask.getName(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getEpicId());
        } else if (task instanceof Epic epic) {
            return String.format("%d,EPIC,%s,%s,%s\n",
                    epic.getId(),
                    epic.getName(),
                    epic.getStatus(),
                    epic.getDescription());
        }

        return String.format("%d,TASK,%s,%s,%s\n",
                task.getId(),
                task.getName(),
                task.getStatus(),
                task.getDescription());
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        if (!file.exists() || file.length() == 0) {
            // Возвращаем пустой менеджер, если файл не существует или пуст
            return fileBackedTaskManager;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headers = reader.readLine(); // Пропускаем заголовок
            if (headers == null || headers.isBlank()) {
                return fileBackedTaskManager; // Пустой файл
            }

            String line = reader.readLine();
            while (line != null) {
                if (line.isBlank()) {
                    line = reader.readLine();
                    continue;
                }

                Task task = parseTask(line);
                if (task instanceof Subtask subtask) {
                    fileBackedTaskManager.saveSubtask(subtask);
                } else if (task instanceof Epic epic) {
                    fileBackedTaskManager.saveEpic(epic);
                } else {
                    fileBackedTaskManager.saveTask(task);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }
        return fileBackedTaskManager;
    }

    private static Task parseTask(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }

        String[] fields = line.split(",");
        if (fields.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }

        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        return switch (type) {
            case "TASK" -> {
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                yield task;
            }
            case "EPIC" -> {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                yield epic;
            }
            case "SUBTASK" -> {
                if (fields.length < 6) {
                    throw new IllegalArgumentException("Подзадача должна содержать ID эпика: " + line);
                }
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setSubtaskId(id);
                subtask.setStatus(status);
                yield subtask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        };
    }

    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        save();
    }

    @Override
    public void saveEpic(Epic epic) {
        super.saveEpic(epic);
        save();
    }

    @Override
    public void saveSubtask(Subtask subtask) {
        super.saveSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}