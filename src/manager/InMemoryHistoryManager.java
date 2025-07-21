package manager;

import task.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> viewingHistoryOfTasks = new HashMap<>();
    private final LinkedTaskList historyTaskManager = new LinkedTaskList();

    @Override
    public void add(Task task) {
        if (viewingHistoryOfTasks.containsKey(task.getId())) {
            Node node = viewingHistoryOfTasks.get(task.getId());

            historyTaskManager.removeNode(node);
        }

        Node node = historyTaskManager.linkLast(task);

        viewingHistoryOfTasks.put(task.getId(), node);
    }

    @Override
    public List<Task> getHistory() {
        return historyTaskManager.getTasks();
    }

    @Override
    public void remove(int id) {
        if (viewingHistoryOfTasks.containsKey(id)) {
            Node node = viewingHistoryOfTasks.get(id);

            historyTaskManager.removeNode(node);
            viewingHistoryOfTasks.remove(id);
        }
    }
}

class LinkedTaskList {
    private Node head;
    private Node tail;
    private int size = 0;

    public Node linkLast(Task task) {
        Node newTail = new Node(task);

        if (head == null) {
            head = newTail;
        } else {
            newTail.prev = tail;
            tail.next = newTail;
        }
        tail = newTail;

        size++;
        return newTail;
    }

    public List<Task> getTasks() {
        ArrayList<Task> historyViewingTask = new ArrayList<>();
        Node node = head;

        while (node != null) {
            historyViewingTask.add(node.data);
            node = node.next;
        }

        return historyViewingTask;
    }

    public void removeNode(Node node) {
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

        size--;
    }
}
