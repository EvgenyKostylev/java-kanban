package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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
