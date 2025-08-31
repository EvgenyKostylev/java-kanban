package manager;

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
        taskList.clear();
        prioritizedTaskList.clear();
    }

    @Override
    public void removeEpics() {
        epicList.clear();
        subtaskList.clear();
        prioritizedTaskList.clear();
    }

    @Override
    public void removeSubtasks() {
        subtaskList.clear();
        prioritizedTaskList.clear();

        getEpics().forEach(epic -> epic.setSubtasks(new ArrayList<>()));
    }

    @Override
    public Task getTask(int id) {
        Task task = taskList.get(id);

        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicList.get(id);

        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtaskList.get(id);

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        if (addTaskToPrioritizedList(task)) {
            int taskHash = task.hashCode();
            return taskList.put(taskHash, task);
        }
        return null;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int epicHash = epic.hashCode();

        return epicList.put(epicHash, epic);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (addTaskToPrioritizedList(subtask)) {
            int subtaskHash = subtask.hashCode();
            Optional<Epic> optionalEpic = Optional.ofNullable(getEpic(subtask.getEpicId()));

            if (optionalEpic.isEmpty()) {
                return null;
            } else {
                subtaskList.put(subtaskHash, subtask);
                optionalEpic.get().addSubtask(subtask);
                return subtask;
            }
        }
        return null;
    }

    @Override
    public Task updateTask(Task task) {
        if (updatePrioritizedTaskInList(taskList.get(task.getId()), task)) {
            return taskList.put(task.getId(), task);
        }
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epic.getSubtasks().forEach(subtask -> subtask.setEpicId(epic.getId()));

        epicList.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (updatePrioritizedTaskInList(subtaskList.get(subtask.getId()), subtask)) {
            epicList.get(subtask.getEpicId()).updateSubtask(subtask);
            subtaskList.put(subtask.getId(), subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public void removeTask(int id) {
        Optional<Task> taskOpt = Optional.ofNullable(getTask(id));

        if (taskOpt.isPresent()) {
            removeTaskFromPrioritizedList(getTask(id));
            taskList.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        Optional<Epic> epicOpt = Optional.ofNullable(epicList.get(id));

        if (epicOpt.isPresent()) {
            epicOpt.get().getSubtasks().forEach(subtask -> {
                removeTaskFromPrioritizedList(subtask);
                subtaskList.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            });
            epicOpt.get().setSubtasks(new ArrayList<>());
            epicList.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Optional<Subtask> subtaskOpt = Optional.ofNullable(subtaskList.get(id));

        if (subtaskOpt.isPresent()) {
            Epic epic = epicList.get(subtaskOpt.get().getEpicId());

            epic.removeSubtask(subtaskOpt.get());
            removeTaskFromPrioritizedList(subtaskOpt.get());
            subtaskList.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTaskList);
    }

    private boolean updatePrioritizedTaskInList(Task oldTask, Task newTask) {
        removeTaskFromPrioritizedList(oldTask);
        return addTaskToPrioritizedList(newTask);
    }

    private boolean addTaskToPrioritizedList(Task task) {
        if (task.getStartTime() == null) {
            return true;
        } else if (!isIntervalBusy(task)) {
            return prioritizedTaskList.add(task);
        } else {
            return false;
        }
    }

    private void removeTaskFromPrioritizedList(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTaskList.remove(task);
        }
    }

    public boolean isIntervalBusy(Task task) {
        Optional<Task> taskOccupiedInterval = prioritizedTaskList.stream()
                .filter(currentTask ->
                        (task.getStartTime().isEqual(currentTask.getStartTime())
                                || task.getStartTime().isBefore(currentTask.getStartTime()))
                                && task.getEndTime().isAfter(currentTask.getStartTime())
                                || (currentTask.getStartTime().isEqual(task.getStartTime())
                                || currentTask.getStartTime().isBefore(task.getStartTime()))
                                && currentTask.getEndTime().isAfter(task.getStartTime()))
                .findAny();

        return taskOccupiedInterval.isPresent();
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }
}