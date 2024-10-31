package manager;

import task.Node;
import task.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Node> historyViewedTasks;

    private static Node head;

    private static Node tail;

    InMemoryHistoryManager() {
        historyViewedTasks = new HashMap<>();
        head = null;
        tail = null;
    }

    private void linkLast(Task task) {
        final Node t = tail;
        final Node newNode = new Node(t, task, null);
        tail = newNode;
        if (t == null) {
            head = newNode;
        } else {
            t.setNext(newNode);
        }
        historyViewedTasks.put(task.getId(), newNode);
    }

    @Override
    public void add(Task task) {
        Node node = historyViewedTasks.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = historyViewedTasks.get(id);
        if (node == null) {
            return;
        }
        historyViewedTasks.remove(id);
        removeNode(node);
    }

    private void removeNode(Node node) {
        final Node next = node.getNext();
        final Node prev = node.getPrev();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }
        node.setData(null);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> arrTask = new ArrayList<>();
        for (Node node : historyViewedTasks.values()) {
            arrTask.add(node.getData());
        }
        return arrTask;
    }
}