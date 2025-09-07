package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Subtask> subtaskList = new ArrayList<>();
    private LocalDateTime endTime;
    private static final int MIN_COMPLETED_SUBTASKS = 0;

    @Override
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

    public List<Subtask> getSubtasks() {
        return subtaskList;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        subtaskList = subtasks.stream()
                .filter(subtask -> subtask.getId() != getId()).collect(Collectors.toList());
        updateStatus();
        updateTime();
    }

    public boolean addSubtask(Subtask subtask) {
        if (subtask.getId() != getId()) {
            subtaskList.add(subtask);
            updateStatus();
            updateTime();
            return true;
        }
        return false;
    }

    public void removeSubtask(Subtask subtask) {
        subtaskList.remove(subtask);
        updateStatus();
        updateTime();
    }

    private void updateStatus() {
        long completedSubtasks = getSubtasks().stream()
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

        if (completedSubtasks == getSubtasks().size() && !(getSubtasks().isEmpty())) {
            setStatus(Status.DONE);
        } else if (completedSubtasks > MIN_COMPLETED_SUBTASKS) {
            setStatus(Status.IN_PROGRESS);
        } else if (completedSubtasks < MIN_COMPLETED_SUBTASKS) {
            setStatus(Status.IN_PROGRESS);
        } else {
            setStatus(Status.NEW);
        }
    }

    private void updateTime() {
        Optional<Subtask> initialSubtask = getSubtasks().stream()
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
            setStartTime(null);
            setDuration(null);
            setEndTime(null);
        } else {
            setDuration(getSubtasks().stream()
                    .map(Task::getDuration)
                    .reduce(Duration::plus)
                    .get());
            setStartTime(initialSubtask.get().getStartTime());
            setEndTime(getStartTime().plus(getDuration()));
        }
    }

    public boolean updateSubtask(Subtask subtask) {
        if (subtask.getId() == getId()) {
            return false;
        } else {
            setSubtasks(getSubtasks().stream()
                    .map(subtaskByEpic -> {
                        if (subtaskByEpic.getId() == subtask.getId()) {
                            return subtask;
                        } else {
                            return subtaskByEpic;
                        }
                    }).collect(Collectors.toList()));
            updateStatus();
            updateTime();
        }
        return true;
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