package ru.practicum.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.practicum.configuration.DurationTypeAdapter;
import ru.practicum.configuration.LocalDateTimeTypeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Класс-утилита Managers предоставляет стандартные методы для создания
 * экземпляров TaskManager, HistoryManager и Gson.
 *
 * <p>Класс является статическим и не позволяет создание экземпляров, так как
 * его методы предназначены для обеспечения единой точки доступа к реализации
 * менеджеров задач и истории.</p>
 */
public class Managers {

    /**
     * Приватный конструктор предотвращает создание экземпляров класса.
     */
    private Managers() {
    }

    /**
     * Создает и возвращает стандартный экземпляр TaskManager, использующий
     * {@link InMemoryTaskManager} для хранения задач в памяти.
     *
     * @return Новый экземпляр TaskManager
     */
    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager(getDefaultHistoryManager());
    }

    /**
     * Создает и возвращает стандартный экземпляр HistoryManager, использующий
     * {@link InMemoryHistoryManager} для хранения истории задач в памяти.
     *
     * @return Новый экземпляр HistoryManager
     */
    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    /**
     * Статический метод, возвращающий сконфигурированный Gson.
     */
    public static Gson getDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }
}