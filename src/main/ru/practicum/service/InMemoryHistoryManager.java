package ru.practicum.service;

import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyNode = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private void linkLast(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        // Удаляем старую запись, если она существует
        if (historyNode.containsKey(task.getId())) {
            removeNode(historyNode.get(task.getId()));
        }

        // Добавляем новый узел в конец
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode; // Если список пустой
        } else {
            oldTail.next = newNode;
        }

        historyNode.put(task.getId(), newNode);
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            result.add(current.element);
            current = current.next;
        }
        return result;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) return;

        final Node<Task> prev = node.prev;
        final Node<Task> next = node.next;

        // Удаляем ссылки на текущий узел
        if (prev == null) {
            head = next; // Если узел первый
        } else {
            prev.next = next;
        }

        if (next == null) {
            tail = prev; // Если узел последний
        } else {
            next.prev = prev;
        }

        // Удаляем из мапы
        historyNode.remove(node.element.getId());

        // Обнуляем ссылки узла для GC
        node.next = null;
        node.prev = null;
    }

    @Override
    public void remove(int id) {
        Node<Task> taskNode = historyNode.get(id);
        if (taskNode != null) {
            removeNode(taskNode);
        }
    }

    static class Node<E> {
        public E element;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E element, Node<E> next) {
            this.prev = prev;
            this.element = element;
            this.next = next;
        }
    }
}