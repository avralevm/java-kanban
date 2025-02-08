package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, Duration.ZERO, null);
        subtasks = new ArrayList<>();
        typeTask = TypeTask.EPIC;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateStatus() {
        boolean statusDONE = true;
        boolean statusNEW = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                statusNEW = false;
            } else if (subtask.getStatus() != Status.DONE) {
                statusDONE = false;
            }
        }

        if (statusDONE) {
            status = Status.DONE;
        } else if (statusNEW) {
            status = Status.NEW;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    public void updateTimeFields() {
        Duration sumDurationSubtasks = subtasks.stream()
                .filter(subtask -> subtask.getDuration() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        duration = sumDurationSubtasks;

        LocalDateTime minStartTime = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Subtask::getStartTime))
                .map(Subtask::getStartTime)
                .orElse(null);
        startTime = minStartTime;

        LocalDateTime maxEndTime = subtasks.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(subtask -> subtask.getEndTime()))
                .map(Subtask::getEndTime)
                .orElse(null);
        endTime = maxEndTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }
}