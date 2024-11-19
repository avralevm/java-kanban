package task;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    protected TypeTask typeTask;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = Status.NEW;
        typeTask = TypeTask.TASK;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public TypeTask getTypeTask() {
        return typeTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

//    public String toString(Task task) {
//        return id + "," +
//                typeTask + "," +
//                title + "," +
//                status + "," +
//                description + ",";
//    }
//
//    private Task fromString(String value) {
//        String[] str = value.split(",");
//        String title = str[2];
//        String description = str[4];
//        Task task = new Task(title, description);
//        return task;
//    }
}