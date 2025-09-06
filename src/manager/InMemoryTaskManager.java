package manager;

import exception.NotAcceptableTimeException;
import exception.NotFoundException;
import task.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTaskList = new TreeSet<>();

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskList.values());
    }

    @Override
    public void removeTasks() {
        taskList.values().forEach(task -> historyManager.remove(task.getId()));
        taskList.clear();
        prioritizedTaskList.clear();
    }

    @Override
    public void removeEpics() {
        epicList.values().forEach(epic -> historyManager.remove(epic.getId()));
        epicList.clear();
        removeSubtasks();
        prioritizedTaskList.clear();
    }

    @Override
    public void removeSubtasks() {
        subtaskList.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtaskList.clear();
        prioritizedTaskList.clear();

        getEpics().forEach(epic -> epic.setSubtasks(new ArrayList<>()));
    }

    @Override
    public Task getTask(int id) {
        Task task = taskList.get(id);

        if (task == null) {
            throw new NotFoundException();
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicList.get(id);

        if (epic == null) {
            throw new NotFoundException();
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtaskList.get(id);

        if (subtask == null) {
            throw new NotFoundException();
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(task.hashCode());
        addTaskToPrioritizedList(task);
        return taskList.put(task.getId(), task);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(epic.hashCode());
        return epicList.put(epic.getId(), epic);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        isIntervalBusy(subtask);

        Epic epic = getEpic(subtask.getEpicId());

        subtask.setId(subtask.hashCode());
        if (epic.addSubtask(subtask)) {
            addTaskToPrioritizedList(subtask);
            return subtaskList.put(subtask.getId(), subtask);
        }
        return null;
    }

    @Override
    public Task updateTask(Task task) {
        updatePrioritizedTaskInList(getTask(task.getId()), task);
        return taskList.put(task.getId(), task);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        return epicList.put(epic.getId(), epic);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        isIntervalBusy(subtask);

        Subtask updatedSubtask = getSubtask(subtask.getId());

        if (getEpic(subtask.getEpicId()).updateSubtask(subtask)) {
            updatePrioritizedTaskInList(updatedSubtask, subtask);
            return subtaskList.put(subtask.getId(), subtask);
        }
        return null;
    }

    @Override
    public Task removeTask(int id) {
        Task task = getTask(id);

        if (task == null) {
            throw new NotFoundException();
        } else {
            removeTaskFromPrioritizedList(getTask(id));
            historyManager.remove(id);
            taskList.remove(id);
            historyManager.remove(id);
            return task;
        }
    }

    @Override
    public Epic removeEpic(int id) {
        Epic epic = epicList.get(id);

        if (epic == null) {
            throw new NotFoundException();
        } else {
            epic.getSubtasks().forEach(subtask -> {
                removeTaskFromPrioritizedList(subtask);
                historyManager.remove(subtask.getId());
                subtaskList.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            });
            epic.setSubtasks(new ArrayList<>());
            historyManager.remove(id);
            epicList.remove(id);
            historyManager.remove(id);
            return epic;
        }
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask subtask = subtaskList.get(id);

        if (subtask == null) {
            throw new NotFoundException();
        } else {
            Epic epic = getEpic(subtask.getEpicId());

            epic.removeSubtask(subtask);
            removeTaskFromPrioritizedList(subtask);
            historyManager.remove(id);
            subtaskList.remove(id);
            historyManager.remove(id);
            return subtask;
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTaskList);
    }

    private void updatePrioritizedTaskInList(Task oldTask, Task newTask) {
        removeTaskFromPrioritizedList(oldTask);
        addTaskToPrioritizedList(newTask);
    }

    private void addTaskToPrioritizedList(Task task) {
        if (task.getStartTime() != null) {
            isIntervalBusy(task);
            prioritizedTaskList.add(task);
        }
    }

    private void removeTaskFromPrioritizedList(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTaskList.remove(task);
        }
    }

    public void isIntervalBusy(Task task) {
        Optional<Task> taskOccupiedInterval = prioritizedTaskList.stream()
                .filter(currentTask ->
                        (task.getStartTime().isEqual(currentTask.getStartTime())
                                || task.getStartTime().isBefore(currentTask.getStartTime()))
                                && task.getEndTime().isAfter(currentTask.getStartTime())
                                || (currentTask.getStartTime().isEqual(task.getStartTime())
                                || currentTask.getStartTime().isBefore(task.getStartTime()))
                                && currentTask.getEndTime().isAfter(task.getStartTime()))
                .findAny();

        if (taskOccupiedInterval.isPresent()) {
            throw new NotAcceptableTimeException();
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }
}