package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int epicId, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (getId() != epicId) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        String result = "Subtask{" +
                "name='" + getName() + '\'';

        if(getDescription() != null) {
            result = result + ", description.length=" + getDescription().length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + getStatus()
                + ", epicId=" + epicId + "}";
    }
}
