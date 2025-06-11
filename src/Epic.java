public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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
