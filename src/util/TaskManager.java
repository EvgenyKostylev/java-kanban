package util;

import task.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private static final int MIN_COMPLETED_SUBTASKS = 0;

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(taskList.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicList.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskList.values());
    }

    public void removeTasks() {
        taskList.clear();
    }

    public void removeEpics() {
        epicList.clear();
        subtaskList.clear();
    }

    public void removeSubtasks() {
        subtaskList.clear();
        ArrayList<Epic> epicList = getEpics();

        for (Epic epic : epicList) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    public Task getTask(int id) {
        return taskList.get(id);
    }

    public Epic getEpic(int id) {
        return epicList.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtaskList.get(id);
    }

    public void createTask(Task task) {
        int taskHash = task.hashCode();

        taskList.put(taskHash, task);
    }

    public void createEpic(Epic epic) {
        int epicHash = epic.hashCode();

        epicList.put(epicHash, epic);
    }

    public void createSubtask(Subtask subtask) {
        int subtaskHash = subtask.hashCode();
        Epic epic = epicList.get(subtask.getEpicId());

        subtaskList.put(subtaskHash, subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
    }

    public void updateTask(int id, Task task) {
        taskList.replace(id, task);
    }

    public void updateEpic(int id, Epic newEpic) {
        Epic epic = getEpic(id);
        ArrayList<Subtask> subtasksByEpic = epic.getSubtasks();

        for (Subtask subtask : subtasksByEpic) {
            subtask.setEpicId(newEpic.getId());
        }
        epicList.replace(id, newEpic);
        updateEpicStatus(newEpic);
    }

    public void updateSubtask(int id, Subtask subtask) {
        Epic epic = epicList.get(subtask.getEpicId());

        epic.updateSubtask(id, subtask);
        subtaskList.replace(id, subtask);
        updateEpicStatus(epic);
    }

    public void removeTask(int id) {
        taskList.remove(id);
    }

    public void removeEpic(int id) {
        Epic epic = epicList.get(id);
        ArrayList<Subtask> subtasksByEpic = epic.getSubtasks();

        for (Subtask subtask : subtasksByEpic) {
            subtaskList.remove(subtask.getId());
        }
        epic.clearSubtasks();
        epicList.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtaskList.get(id);
        Epic epic = epicList.get(subtask.getEpicId());

        subtaskList.remove(id);
        epic.removeSubtask(subtask);
        updateEpicStatus(epic);
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksByEpic = epic.getSubtasks();
        int completedSubtasks = 0;

        for (Subtask subtask : subtasksByEpic) {
            if (subtask.getStatus() == Status.DONE) {
                completedSubtasks++;
            }
        }

        if (completedSubtasks == subtasksByEpic.size() && !(subtasksByEpic.isEmpty())) {
            epic.setStatus(Status.DONE);
        } else if (completedSubtasks > MIN_COMPLETED_SUBTASKS){
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }
}
