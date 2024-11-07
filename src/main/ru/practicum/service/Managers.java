package ru.practicum.service;

/**
 * Класс-утилита Managers предоставляет стандартные методы для создания
 * экземпляров TaskManager и HistoryManager.
 *
 * <p>Класс является статическим и не позволяет создание экземпляров, так как
 * его методы предназначены для обеспечения единой точки доступа к реализации
 * менеджеров задач и истории.</p>
 */
public class Managers {

    /**
     * Приватный конструктор предотвращает создание экземпляров класса.
     */
    private Managers() {}

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
}