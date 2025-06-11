public class Subtask extends Task {
    private int epicId;

    public Subtask(int epicId, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public String getDescription() {
        return super.getDescription();
    }

    public void setDescription(String description) {
        super.setDescription(description);
    }

    public int getId() {
        return super.getId();
    }

    public void setId(int id) {
        super.setId(id);
    }

    public Status getStatus() {
        return super.getStatus();
    }

    public void setStatus(Status status) {
        super.setStatus(status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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
