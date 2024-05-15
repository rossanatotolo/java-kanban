package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    @Override
    public void add(Task task) {
        // добаляет новую запись в мапу
        if (task != null) {
            int id = task.getId();
            if (!historyMap.isEmpty() && historyMap.containsKey(id)) {
                remove(id);
            }
            historyMap.put(id, linkLast(task));
        }
    }

    @Override
    public List<Task> getHistory() {
        //собирает все задачи в лист
        List<Task> list = new ArrayList<>();
        Node<Task> newNode = first;
        while (newNode != null) {
            list.add(newNode.task);
            newNode = newNode.next;
        }
        return list;
    }

    @Override
    public void remove(int id) {
        //удаляет задачу из приложения и истории
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    private Node<Task> linkLast(Task task) {
        //добавляет задачу в конец двусвязанного списка
        final Node<Task> lastNode = last;
        final Node<Task> newNode = new Node<Task>(task, null, lastNode);
        last = newNode;
        if (lastNode == null) {
            newNode.prev = null;
            first = newNode;
        } else {
            lastNode.next = newNode;
        }
        return newNode;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;
        if (prev != null) {
            prev.next = node.next;
        } else {
            next.prev = null;
            first = node.next;
        }
        if (next != null) {
            next.prev = node.prev;
        } else {
            prev.next = null;
            last = node.prev;
        }
    }

    private static class Node<T extends Task> {
        public T task;
        public Node<T> next;
        public Node<T> prev;

        public Node(T task, Node<T> next, Node<T> prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}


