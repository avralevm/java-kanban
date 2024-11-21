package manager;

import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest {
    static FileBackedTaskManager fileBackedTaskManager;
    static Path path;
    static File file;
    static Task task, task2;
    static Epic epic, epic2;
    static Subtask subtask, subtask2;

    @BeforeAll
    static void createTempFile() {
        try {
            path = Files.createTempFile("FileSave", ".csv");
            file = path.toFile();
            fileBackedTaskManager = new FileBackedTaskManager(file);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @AfterAll
    static  void deleteTempFile() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    void createTask() {
        task = new Task("1", "Задача 1");
        fileBackedTaskManager.createTask(task);
        task2 = new Task("2", "Задача 2");
        fileBackedTaskManager.createTask(task2);
        epic = new Epic("3", "Эпик 3");
        fileBackedTaskManager.createEpic(epic);
        epic2 = new Epic("4", "Эпик 4");
        fileBackedTaskManager.createEpic(epic2);
        subtask = new Subtask("5", "Сабтаск 5", epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
        subtask2 = new Subtask("6", "Сабтаск 6", epic.getId());
        fileBackedTaskManager.createSubtask(subtask);
    }

    @Test
    void saveAndLoadTaskTest() {
        createTask();

        Task task = new Task("Задача 1", "Обновление Задачи 1");
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