package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final int MIN_COMPLETED_SUBTASKS = 0;

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
    }

    @Override
    public void removeEpics() {
        epicList.clear();
        subtaskList.clear();
    }

    @Override
    public void removeSubtasks() {
        subtaskList.clear();

        ArrayList<Epic> epicList = getEpics();

        for (Epic epic : epicList) {
            epic.setSubtasks(new ArrayList<>());
            updateEpicStatus(epic);
        }
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
        int taskHash = task.hashCode();

        return taskList.put(taskHash, task);
    }

    @Override
    public Epic createEpic(Epic epic) {
        int epicHash = epic.hashCode();

        return epicList.put(epicHash, epic);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int subtaskHash = subtask.hashCode();
        Epic epic = epicList.get(subtask.getEpicId());
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);

        subtaskList.put(subtaskHash, subtask);
        subtasksByEpic.add(subtask);
        epic.setSubtasks(subtasksByEpic);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        return taskList.put(task.getId(), task);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);

        for (Subtask subtask : subtasksByEpic) {
            subtask.setEpicId(epic.getId());
        }
        epicList.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Epic epic = epicList.get(subtask.getEpicId());
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);

        for (int i = 0; i < subtasksByEpic.size(); i++) {
            Subtask currentSubtask = subtasksByEpic.get(i);

            if (currentSubtask.getId() == subtask.getId()) {
                subtasksByEpic.set(i, subtask);
            }
        }
        epic.setSubtasks(subtasksByEpic);
        subtaskList.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void removeTask(int id) {
        taskList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epicList.get(id);
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);

        for (Subtask subtask : subtasksByEpic) {
            subtaskList.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epic.setSubtasks(new ArrayList<>());
        epicList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtaskList.get(id);
        Epic epic = epicList.get(subtask.getEpicId());
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);

        subtasksByEpic.remove(subtask);
        epic.setSubtasks(subtasksByEpic);
        subtaskList.remove(id);
        historyManager.remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtaskListByEpic = new ArrayList<>();

        for (Subtask subtask : subtaskList.values()) {
            for (int subtaskId : epic.getSubtasks()) {
                if (subtask.getId() == subtaskId) {
                    subtaskListByEpic.add(subtask);
                }
            }
        }

        return subtaskListByEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Epic updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);
        int completedSubtasks = 0;

        for (Subtask subtask : subtasksByEpic) {
            if (subtask.getStatus() == Status.DONE) {
                completedSubtasks++;
            }
        }

        if (completedSubtasks == subtasksByEpic.size() && !(subtasksByEpic.isEmpty())) {
            epic.setStatus(Status.DONE);
        } else if (completedSubtasks > MIN_COMPLETED_SUBTASKS) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
        return epic;
    }
}
