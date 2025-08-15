package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(Task task) {
        super(task);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtaskIdList;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        subtaskIdList.clear();
        for (Subtask subtask : subtasks) {
            if (subtask.getId() != getId()) {
                subtaskIdList.add(subtask.getId());
            }
        }
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        subtaskIdList.clear();
        for (Integer subtaskId : subtasksId) {
            if (subtaskId != getId()) {
                subtaskIdList.add(subtaskId);
            }
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + getName() + '\'';

        if (getDescription() != null) {
            result = result + ", description.length=" + getDescription().length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + getStatus() + "}";
    }
}
