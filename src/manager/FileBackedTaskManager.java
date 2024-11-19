package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File autoSaveFile;

    public FileBackedTaskManager(File autoSaveFile) {
        this.autoSaveFile = autoSaveFile;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSaveFile))) {
            writer.write("id,type,name,status,description,epic");

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

    public static FileBackedTaskManager loadFromFile(File autoSaveFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(autoSaveFile);
        int maxID = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(autoSaveFile))) {
            String header = reader.readLine(); // Заглушка, чтобы не выпадала ошибка NullPointerException, так как первая строка header

            while (reader.ready()) {
                Task task = fileBackedTaskManager.fromString(reader.readLine());
                switch (task.getTypeTask()) {
                    case TASK : {
                        fileBackedTaskManager.setCountID(task.getId());
                        fileBackedTaskManager.createTask(task);
                        if (maxID < task.getId()) {
                            maxID = task.getId();
                        }
                        break;
                    }
                    case EPIC: {
                        Epic epic = (Epic) task;
                        fileBackedTaskManager.setCountID(epic.getId());
                        fileBackedTaskManager.createEpic(epic);
                        if (maxID < epic.getId()) {
                            maxID = epic.getId();
                        }
                        break;
                    }
                    case SUBTASK: {
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.setCountID(subtask.getId());
                        fileBackedTaskManager.createSubtask(subtask);
                        if (maxID < subtask.getId()) {
                            maxID = subtask.getId();
                        }
                        break;
                    }
                }
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

    // метод toString стоит переопределить для каждого вида Task или так оставить,
    private String toString(Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(task.getId() + "," +
                        task.getTypeTask() + "," +
                        task.getTitle() + "," +
                        task.getStatus() + "," +
                        task.getDescription() + ",");
        if (task.getTypeTask() == TypeTask.SUBTASK) {
            Subtask subtask = (Subtask) task;
            stringBuilder.append(subtask.getEpicId());
        }
        return stringBuilder.toString();
    }

    // метод fromString стоит ли переопределить для каждого вида Task или так оставить
    public Task fromString(String value) {
        String[] taskInfo = value.split(",");
        Integer id = Integer.valueOf(taskInfo[0]);
        String typeTask = taskInfo[1];
        String title = taskInfo[2];
        Status status = Status.valueOf(taskInfo[3]);
        String description = taskInfo[4];

        switch (typeTask) {
            case "TASK": {
                Task task = new Task(title, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            }
            case "EPIC": {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            }
            case "SUBTASK": {
                Integer epicId = Integer.valueOf(taskInfo[5]);
                Subtask subtask = new Subtask(title, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            }
            default: {
                return null;
            }
        }
    }

    public static void main(String[] args) {

        File file = new File("java-kanban\\src\\FileSave.csv");

        FileBackedTaskManager.loadFromFile(file);

        FileBackedTaskManager fileBacked = new FileBackedTaskManager(file);

        Task task1 = new Task("1", "Задача 1");
        fileBacked.createTask(task1);


        Task task2 = new Task("2", "Задача 2");
        fileBacked.createTask(task2);

        Epic epic1 = new Epic("3", "Эпик 3");
        fileBacked.createEpic(epic1);

        Subtask subtask1 = new Subtask("4", "Сабтаск 4", epic1.getId());
        fileBacked.createSubtask(subtask1);

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        for (Task task :  fileBackedTaskManager.getAllTasks()) {
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

