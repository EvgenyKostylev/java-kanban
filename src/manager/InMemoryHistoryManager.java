package manager;

import task.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> viewingHistoryOfTasks = new HashMap<>();
    private Node head;
    private Node tail;
    private int sizeOfHistory = 0;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (viewingHistoryOfTasks.containsKey(task.getId())) {
            Node node = viewingHistoryOfTasks.get(task.getId());

            removeNode(node);
        }

        Node node = linkLast(task);

        viewingHistoryOfTasks.put(task.getId(), node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (viewingHistoryOfTasks.containsKey(id)) {
            Node node = viewingHistoryOfTasks.get(id);

            removeNode(node);
            viewingHistoryOfTasks.remove(id);
        }
    }

    private Node linkLast(Task task) {
        Node newTail = new Node(task);

        if (head == null) {
            head = newTail;
        } else {
            newTail.prev = tail;
            tail.next = newTail;
        }
        tail = newTail;
        sizeOfHistory++;
        return newTail;
    }

    private List<Task> getTasks() {
        ArrayList<Task> historyViewingTask = new ArrayList<>();
        Node node = head;

        while (node != null) {
            historyViewingTask.add(node.data);
            node = node.next;
        }
        return historyViewingTask;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
            return;
        } else if (node.prev == null) {
            head = nextNode;
            nextNode.prev = null;
        } else if (node.next == null) {
            tail = prevNode;
            prevNode.next = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
        sizeOfHistory--;
    }
}