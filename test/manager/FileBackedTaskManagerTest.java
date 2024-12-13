package manager;

import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    static FileBackedTaskManager fileBackedTaskManager;
    static File file;
    static Task task1, task2;
    static Epic epic, epic2;
    static Subtask subtask, subtask2;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
        file = Files.createTempFile("FileSave", ".csv").toFile();
        file.deleteOnExit();
        fileBackedTaskManager = new FileBackedTaskManager(file);
        return fileBackedTaskManager;
    }

    void createTask() {
        task1 = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        fileBackedTaskManager.createTask(task1);

        task2 = new Task("2", "Задача 2", Duration.ofMinutes(30),
                task1.getStartTime().plusHours(2));
        fileBackedTaskManager.createTask(task2);

        epic = new Epic("3", "Эпик 3");
        fileBackedTaskManager.createEpic(epic);

        epic2 = new Epic("4", "Эпик 4");
        fileBackedTaskManager.createEpic(epic2);

        subtask = new Subtask("5", "Сабтаск 5", epic.getId(), Duration.ofMinutes(15),
                task1.getEndTime().plusHours(1));
        fileBackedTaskManager.createSubtask(subtask);

        subtask2 = new Subtask("6", "Сабтаск 6", epic.getId(), Duration.ofMinutes(45),
                subtask.getEndTime().plusMinutes(15));
        fileBackedTaskManager.createSubtask(subtask2);
    }

    @Test
    void saveAndLoadTaskTest() {
        createTask();

        Task task = new Task("Задача 1", "Обновление Задачи 1", Duration.ofMinutes(45),
                task1.getStartTime().plusHours(1));
        fileBackedTaskManager.updateTask(task);
        fileBackedTaskManager.removeTask(task2.getId());
        fileBackedTaskManager.removeEpic(epic2.getId());
        fileBackedTaskManager.removeAllSubtasks();

        FileBackedTaskManager loadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNotNull(loadFileBackedTaskManager);
        Task task1 = loadFileBackedTaskManager.getTaskById(task.getId());
        Assertions.assertEquals(task, task1,"Таски не совпадают, неверно загружен менеджер");
        Assertions.assertNull(loadFileBackedTaskManager.getTaskById(epic2.getId()));
        Assertions.assertEquals(0, loadFileBackedTaskManager.getAllSubtasks().size());
    }
}