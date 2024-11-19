package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
        typeTask = TypeTask.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

//    @Override
//    public String toString(Task task) {
//        return id + "," +
//                typeTask + "," +
//                title + "," +
//                status + "," +
//                description + "," +
//                epicId;
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
