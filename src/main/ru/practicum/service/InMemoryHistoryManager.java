package ru.practicum.service;

import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> hisoryNode = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private void linkLast(Task element) {
        if (hisoryNode.containsKey(element.getId())) {
            removeNode(hisoryNode.get(element.getId()));
        }

        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) head = newNode;
        else oldTail.next = newNode;
        hisoryNode.put(element.getId(), newNode);
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

        if (prev == null) head = next;
        else prev.next = next;

        if (next == null) tail = prev;
        else next.prev = prev;

        node.next = null;
        node.prev = null;
    }

    @Override
    public void remove(int id) {
        Node<Task> taskNode = hisoryNode.get(id);
        if (taskNode != null) removeNode(taskNode);
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