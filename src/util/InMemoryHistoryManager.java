package util;

import task.*;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final List<Task> viewingHistoryOfTasks = new ArrayList<>();
    private static final int MAX_SIZE_OF_HISTORY = 10;

    @Override
    public void add(Task task) {
        if (viewingHistoryOfTasks.size() >= MAX_SIZE_OF_HISTORY) {
            viewingHistoryOfTasks.removeFirst();
        }
        viewingHistoryOfTasks.add(new Task(task.getName(), task.getDescription(), task.getStatus()));
    }

    @Override
    public List<Task> getHistory() {
        return viewingHistoryOfTasks;
    }
}
