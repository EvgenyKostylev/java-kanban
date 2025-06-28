package task;

import java.util.ArrayList;

public class Epic extends Task {
    private static final ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtaskList;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        subtaskList.clear();
        for (Subtask subtask : subtasks) {
            if (subtask.getId() != getId()) {
                subtaskList.add(subtask);
            }
        }
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + getName() + '\'';

        if(getDescription() != null) {
            result = result + ", description.length=" + getDescription().length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + getStatus() + "}";
    }
}
