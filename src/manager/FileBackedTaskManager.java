package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File autoSaveFile;

    public FileBackedTaskManager(File autoSaveFile) {
        this.autoSaveFile = autoSaveFile;
    }

    public static FileBackedTaskManager loadFromFile(File autoSaveFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(autoSaveFile);
        int maxID = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(autoSaveFile))) {
            String header = reader.readLine(); // Заглушка, чтобы не выпадала ошибка NullPointerException, так как первая строка header

            while (reader.ready()) {
                Task task = fileBackedTaskManager.fromString(reader.readLine());
                fileBackedTaskManager.setCountID(task.getId());
                switch (task.getTypeTask()) {
                    case TASK: {
                        fileBackedTaskManager.createTask(task);
                        break;
                    }
                    case EPIC: {
                        fileBackedTaskManager.createEpic((Epic) task);
                        break;
                    }
                    case SUBTASK: {
                        fileBackedTaskManager.createSubtask((Subtask) task);
                        break;
                    }
                }
                maxID = Math.max(maxID, task.getId());
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка загрузки Task", exception);
        } finally {
            fileBackedTaskManager.setCountID(++maxID);
        }
        return fileBackedTaskManager;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private String toString(Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(task.getId() + "," +
                task.getTypeTask() + "," +
                task.getTitle() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getDuration() + "," +
                task.getStartTime());
        if (task.getTypeTask() == TypeTask.SUBTASK) {
            Subtask subtask = (Subtask) task;
            stringBuilder.append("," + subtask.getEpicId());
        }
        return stringBuilder.toString();
    }

    private Task fromString(String value) {
        String[] taskInfo = value.split(",");
        Integer id = Integer.valueOf(taskInfo[0]);
        String typeTask = taskInfo[1];
        String title = taskInfo[2];
        Status status = Status.valueOf(taskInfo[3]);
        String description = taskInfo[4];
        Duration duration = Duration.parse(taskInfo[5]);
        LocalDateTime startTime = LocalDateTime.parse(taskInfo[6]);

        Task task = null;
        switch (typeTask) {
            case "TASK": {
                task = new Task(title, description, duration, startTime);
                break;
            }
            case "EPIC": {
                task = new Epic(title, description);
                break;
            }
            case "SUBTASK": {
                Integer epicId = Integer.valueOf(taskInfo[7]);
                task = new Subtask(title, description, epicId, duration, startTime);
                break;
            }
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSaveFile))) {
            writer.write("id,type,name,status,description,duration,startTime,epic");

            for (Task task : tasks.values()) {
                writer.write("\n" + toString(task));
            }
            for (Epic epic : epics.values()) {
                writer.write("\n" + toString(epic));
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write("\n" + toString(subtask));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка сохранения Task", exception);
        }
    }

    private void setCountID(int countID) {
        this.countID = countID;
    }

    public static void main(String[] args) {
        File file = new File("src\\FileSave.csv");

        FileBackedTaskManager fileBacked = new FileBackedTaskManager(file);

        Task task1 = new Task("1", "Задача 1", Duration.ofMinutes(30),
                LocalDateTime.of(2021, 12, 6, 22, 36));
        fileBacked.createTask(task1);

        Task task2 = new Task("2", "Задача 2", Duration.ofHours(1),
                LocalDateTime.of(2024, Month.DECEMBER, 6, 23, 5));
        fileBacked.createTask(task2);

        Epic epic1 = new Epic("3", "Эпик 3");
        fileBacked.createEpic(epic1);

        Subtask subtask1 = new Subtask("4", "Сабтаск 4", epic1.getId(), Duration.ofDays(30),
                LocalDateTime.of(2020, 1, 1, 1, 1));
        fileBacked.createSubtask(subtask1);

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        for (Task task : fileBackedTaskManager.getAllTasks()) {
            System.out.println(task + "\n");
        }
        for (Epic epic : fileBackedTaskManager.getAllEpics()) {
            System.out.println(epic + "\n");
        }
        for (Subtask subtask : fileBackedTaskManager.getAllSubtasks()) {
            System.out.println(subtask + "\n");
        }
    }
}