package manager;

import task.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic newEpic);

    Subtask updateSubtask(Subtask subtask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    List<Subtask> getSubtasksByEpic(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
