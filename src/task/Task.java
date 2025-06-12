package task;

import util.Status;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        id = hashCode();
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Task otherTask = (Task) object;

        return Objects.equals(name, otherTask.name) &&
                Objects.equals(description, otherTask.description);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "name='" + name + '\'';

        if(description != null) {
            result = result + ", description.length=" + description.length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + status + "}";
    }

    @Override
    public int hashCode() {
        int hash = 17;

        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;
        if (description != null) {
            hash = hash + description.hashCode();
        }
        return hash;
    }
}
