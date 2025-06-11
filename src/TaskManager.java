import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    }

    public void removeSubtasks() {
        subtaskList.clear();
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
        checkEpicStatus(epic);
    }

    public void updateTask(int id, Task task) {
        taskList.replace(id, task);
    }

    public void updateEpic(int id, Epic newEpic) {
        Epic epic = getEpic(id);
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);

        for (Subtask subtask : subtasksByEpic) {
            subtask.setEpicId(newEpic.getId());
        }
        epicList.replace(id, newEpic);
        checkEpicStatus(newEpic);
    }

    public void updateSubtask(int id, Subtask subtask) {
        Epic epic = epicList.get(subtask.getEpicId());

        subtaskList.replace(id, subtask);
        checkEpicStatus(epic);
    }

    public void removeTask(int id) {
        taskList.remove(id);
    }

    public void removeEpic(int id) {
        Epic epic = epicList.get(id);
        List<Subtask> subtasksByEpic = new ArrayList<>(getSubtasksByEpic(epic));
        for (Subtask subtask : subtasksByEpic) {
            subtaskList.remove(subtask.getId());
        }
        epicList.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtaskList.get(id);
        Epic epic = epicList.get(subtask.getEpicId());

        subtaskList.remove(id);
        checkEpicStatus(epic);
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasks = getSubtasks();
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            if (subtask.getEpicId() == epic.getId()) {
                subtasksByEpic.add(subtask);
            }
        }
        return subtasksByEpic;
    }

    public void checkEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasksByEpic = getSubtasksByEpic(epic);
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
