package task;

import util.Status;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtaskList;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtaskList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public void updateSubtask(int id, Subtask newSubtask) {
        for (int i = 0; i < subtaskList.size(); i++) {
            Subtask subtask = subtaskList.get(i);
            if (subtask.getId() == id) {
                subtaskList.set(i, newSubtask);
            }
        }
    }

    public void removeSubtask(Subtask subtask) {
        subtaskList.remove(subtask);
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
