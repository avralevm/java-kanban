package manager;


import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileBackedTaskManagerTest {
    static FileBackedTaskManager fileBackedTaskManager;
    static Path path;
    static File file;
    static Task task, task2;
    static Epic epic;
    static Subtask subtask;

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
        fileBackedTaskManager.createTask(epic);
        subtask = new Subtask("4", "Сабтаск 4", epic.getId());
        fileBackedTaskManager.createTask(subtask);
    }

    @Test
    @Order(0)
    void loadEmptyFileTest() {
        Assertions.assertTrue(file.exists(), "Файла не существуeт");
        Assertions.assertEquals(0, file.length(), "Файл должен быть пустым");
    }

    @Test
    @Order(1)
    void saveEmptyFileTest() {
        fileBackedTaskManager.save();
        Assertions.assertTrue(file.exists(), "Файла не существуeт");
        try {
            String[] elementsFileSave = Files.readString(path).split("\n");
            Assertions.assertEquals(1,elementsFileSave.length);
            Assertions.assertEquals("id,type,name,status,description,epic", elementsFileSave[0], "Шапка не создаётся");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    @Order(3)
    void saveSomeTaskTest() {
        createTask();
        fileBackedTaskManager.save();

        try {
            String[] elementsFileSave = Files.readString(path).split("\n");
            Assertions.assertEquals("id,type,name,status,description,epic", elementsFileSave[0], "Шапка не создаётся");
            Assertions.assertEquals(task, fileBackedTaskManager.fromString(elementsFileSave[1]),"Первая задач не cохранилась");
            Assertions.assertEquals(subtask, fileBackedTaskManager.fromString(elementsFileSave[4]),"Последняя задача не cохранилась");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    @Order(4)
    void loadSomeTaskTest() {
        FileBackedTaskManager fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(file);

        Task receivedTask = fileBackedTaskManager2.getTaskById(0);
        Task receivedEpic = fileBackedTaskManager2.getEpicById(2);
        Task receivedSubtask = fileBackedTaskManager2.getSubtaskById(3);

        Assertions.assertEquals(task, receivedTask);
        Assertions.assertEquals(epic, receivedEpic);
        Assertions.assertEquals(subtask, receivedSubtask);

        try {
            String[] elementsFileSave = Files.readString(path).split("\n");
            Assertions.assertEquals(5, elementsFileSave.length);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}