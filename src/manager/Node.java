package manager;

import task.Task;

public class Node {
    public Task data;
    public Node next;
    public Node prev;

    public Node(Task task) {
        this.data = task;
        this.next = null;
        this.prev = null;
    }
}
