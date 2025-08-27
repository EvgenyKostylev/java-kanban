package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
    }

    public Epic(Task task) {
        super(task);
    }

    public List<Integer> getSubtasks() {
        return subtaskIdList;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        subtaskIdList.clear();
        subtasks.stream()
                .filter(subtask -> subtask.getId() != getId())
                .forEach(subtask -> subtaskIdList.add(subtask.getId()));
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        subtaskIdList.clear();
        subtasksId.stream()
                .filter(subtaskId -> subtaskId != getId())
                .forEach(subtaskIdList::add);
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
