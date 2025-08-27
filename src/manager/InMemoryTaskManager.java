package manager;

import task.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final int MIN_COMPLETED_SUBTASKS = 0;
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

        getEpics().forEach(epic -> {
            epic.setSubtasks(new ArrayList<>());
            updateEpicStatus(epic);
            updateEpicTime(epic);
        });
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
            Optional<Epic> optionalEpic = Optional.ofNullable(epicList.get(subtask.getEpicId()));

            if (optionalEpic.isEmpty()) {
                return null;
            } else {
                List<Subtask> subtasksByEpic = getSubtasksByEpic(optionalEpic.get());

                subtaskList.put(subtaskHash, subtask);
                subtasksByEpic.add(subtask);
                optionalEpic.get().setSubtasks(subtasksByEpic);
                updateEpicStatus(optionalEpic.get());
                updateEpicTime(optionalEpic.get());
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
        getSubtasksByEpic(epic).forEach(subtask -> subtask.setEpicId(epic.getId()));

        epicList.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (updatePrioritizedTaskInList(subtaskList.get(subtask.getId()), subtask)) {
            Epic epic = epicList.get(subtask.getEpicId());

            updateEpicSubtask(epic, subtask);
            subtaskList.put(subtask.getId(), subtask);
            updateEpicStatus(epic);
            updateEpicTime(epic);
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
            getSubtasksByEpic(epicOpt.get()).forEach(subtask -> {
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
            List<Subtask> subtasksByEpic = getSubtasksByEpic(epic);

            subtasksByEpic.remove(subtaskOpt.get());
            epic.setSubtasks(subtasksByEpic);
            removeTaskFromPrioritizedList(subtaskOpt.get());
            subtaskList.remove(id);
            historyManager.remove(id);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return subtaskList.values()
                .stream()
                .filter(subtask -> epic.getSubtasks().contains(subtask.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Epic updateEpicStatus(Epic epic) {
        long completedSubtasks = getSubtasksByEpic(epic).stream()
                .map(Subtask::getStatus)
                .reduce(0L, (count, status) -> {
                    if (status.equals(Status.IN_PROGRESS)) {
                        return -1L;
                    } else if (status.equals(Status.DONE)) {
                        return count == -1 ? -1 : count + 1;
                    } else {
                        return count;
                    }
                }, (a, b) -> a == -1 || b == -1 ? -1 : a + b);

        if (completedSubtasks == getSubtasksByEpic(epic).size() && !(getSubtasksByEpic(epic).isEmpty())) {
            epic.setStatus(Status.DONE);
        } else if (completedSubtasks > MIN_COMPLETED_SUBTASKS) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (completedSubtasks < MIN_COMPLETED_SUBTASKS) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
        return epic;
    }

    private Epic updateEpicTime(Epic epic) {
        List<Subtask> subtasksByEpic = getSubtasksByEpic(epic);
        Optional<Subtask> initialSubtask = subtasksByEpic.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min((firstSubtask, secondSubtask) -> {
                    if (firstSubtask.getEndTime().isBefore(secondSubtask.getEndTime())) {
                        return -1;
                    } else if (firstSubtask.getEndTime().isAfter(secondSubtask.getEndTime())) {
                        return 1;
                    } else {
                        return 0;
                    }
                });

        if (initialSubtask.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return epic;
        }
        epic.setDuration(subtasksByEpic.stream()
                .map(Task::getDuration)
                .reduce(Duration::plus)
                .get());
        epic.setStartTime(initialSubtask.get().getStartTime());
        epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        return epic;
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

    public void updateEpicSubtask(Epic epic, Subtask subtask) {
        epic.setSubtasks(getSubtasksByEpic(epic).stream()
                .map(subtaskByEpic -> {
                    if (subtaskByEpic.getId() == subtask.getId()) {
                        return subtask;
                    } else {
                        return subtaskByEpic;
                    }
                }).collect(Collectors.toList()));
    }
}