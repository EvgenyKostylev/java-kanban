package manager;

import exception.NotAcceptableTimeException;
import exception.NotFoundException;
import task.*;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getTask(int id) throws NotFoundException;

    Epic getEpic(int id) throws NotFoundException;

    Subtask getSubtask(int id) throws NotFoundException;

    Task createTask(Task task) throws NotAcceptableTimeException;

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask) throws NotAcceptableTimeException;

    Task updateTask(Task task) throws NotAcceptableTimeException;

    Epic updateEpic(Epic newEpic);

    Subtask updateSubtask(Subtask subtask) throws NotAcceptableTimeException;

    Task removeTask(int id) throws NotFoundException;

    Epic removeEpic(int id) throws NotFoundException;

    Subtask removeSubtask(int id) throws NotFoundException;

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Subtask> getSubtasksByEpic(Epic epic);
}