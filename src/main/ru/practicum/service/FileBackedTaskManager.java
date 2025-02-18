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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private boolean isLoading = false;

    /**
     * Конструктор инициализирует новый FileBackedTaskManager с файлом для автосохранения.
     *
     * @param file файл для сохранения данных
     */
    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    /**
     * Сохранение данных в файл.
     */
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write("id,type,name,status,description,epic,startTime,duration,epicId\n");

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

    /**
     * Преобразование задачи в строку для записи в файл.
     */
    private String taskToString(Task task) {
        Status status = task.getStatus() != null ? task.getStatus() : Status.NEW;
        if (task instanceof Subtask subtask) {
            return String.format("%d,SUBTASK,%s,%s,%s,%s,%s,%d\n",
                    subtask.getId(),
                    subtask.getName(),
                    status,
                    subtask.getDescription(),
                    subtask.getStartTime(),
                    subtask.getDuration(),
                    subtask.getEpicId());
        } else if (task instanceof Epic epic) {
            return String.format("%d,EPIC,%s,%s,%s,%s,%s\n",
                    epic.getId(),
                    epic.getName(),
                    status,
                    epic.getDescription(),
                    epic.getStartTime(),
                    epic.getDuration());
        }

        return String.format("%d,TASK,%s,%s,%s,%s,%s\n",
                task.getId(),
                task.getName(),
                status,
                task.getDescription(),
                task.getStartTime(),
                task.getDuration());
    }

    /**
     * Загрузка задач из файла.
     *
     * @param file файл с сохранёнными данными
     * @return объект FileBackedTaskManager с загруженными данными
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        if (!file.exists() || file.length() == 0) {
            return manager; // Пустой файл
        }

        manager.isLoading = true;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок

            List<String> subtasksToLoad = new ArrayList<>(); // Временный список для подзадач

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                try {
                    String[] fields = line.split(",");
                    if (fields.length < 7) {
                        throw new IllegalArgumentException("Некорректный формат строки: недостаточно данных");
                    }

                    String type = fields[1];

                    switch (type) {
                        case "TASK" -> {
                            Task task = parseTask(line);
                            manager.saveTask(task);
                        }
                        case "EPIC" -> {
                            Epic epic = (Epic) parseTask(line);
                            manager.saveEpic(epic);
                        }
                        case "SUBTASK" -> subtasksToLoad.add(line); // Откладываем загрузку подзадач
                        default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
                    }
                } catch (IllegalArgumentException e) {
                    throw new ManagerSaveException("Ошибка при разборе строки: " + line, e);
                }
            }

            // 🔹 Теперь загружаем подзадачи, когда все эпики уже есть
            for (String subtaskLine : subtasksToLoad) {
                try {
                    Subtask subtask = (Subtask) parseTask(subtaskLine);
                    manager.saveSubtask(subtask); // Теперь эпик точно должен существовать
                } catch (IllegalArgumentException e) {
                    throw new ManagerSaveException("Ошибка при разборе подзадачи: " + subtaskLine, e);
                }
            }

            // 🔹 Пересчёт времени для всех эпиков
            for (Epic epic : manager.getAllEpics()) {
                manager.updateEpicTime(epic);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        } finally {
            manager.isLoading = false;
        }

        return manager;
    }

    private static Task parseTask(String line) {
        System.out.println("Разбираем строку: " + line); // Дебаг

        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }

        String[] fields = line.split(",");
        if (fields.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }

        Integer id = fields[0].equals("null") ? null : Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Status status = fields[3].equals("null") ? null : Status.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].equals("null") ? null : LocalDateTime.parse(fields[5]);
        Duration duration = fields[6].equals("null") ? Duration.ZERO : Duration.parse(fields[6]);

        System.out.println("Создан объект: " + id + " " + type + " " + name); // Дебаг

        return switch (type) {
            case "TASK" -> {
                Task task = new Task(id, name, description, duration, startTime);
                task.setId(id);
                task.setStatus(status != null ? status : Status.NEW);
                yield task;
            }
            case "EPIC" -> {
                Epic epic = new Epic(name, description, duration, startTime);
                if (id != null) {
                    epic.setId(id);
                }
                epic.setStatus(status != null ? status : Status.NEW);
                yield epic;
            }
            case "SUBTASK" -> {
                if (fields.length < 8) {
                    throw new IllegalArgumentException("Подзадача должна содержать ID эпика: " + line);
                }
                int epicId = Integer.parseInt(fields[7]);
                Subtask subtask = new Subtask(name, description, duration, startTime, epicId);
                if (id != null) {
                    subtask.setId(id);
                }
                subtask.setStatus(status != null ? status : Status.NEW);
                yield subtask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        };
    }

    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        if (!isLoading) save();
    }

    @Override
    public void saveEpic(Epic epic) {
        System.out.println("Сохраняем эпик: " + epic.getId());
        super.saveEpic(epic);
        if (!isLoading) save();
    }

    @Override
    public void saveSubtask(Subtask subtask) {
        super.saveSubtask(subtask);
        if (!isLoading) save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        if (!isLoading) save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        if (!isLoading) save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        if (!isLoading) save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        if (!isLoading) save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        if (!isLoading) save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        if (!isLoading) save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        if (!isLoading) save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        if (!isLoading) save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        if (!isLoading) save();
    }
}
