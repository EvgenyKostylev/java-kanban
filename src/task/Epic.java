package task;

import util.Status;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtaskList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtaskList;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtaskList = subtasks;
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
