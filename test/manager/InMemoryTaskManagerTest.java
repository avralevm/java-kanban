package manager;

import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    public static TaskManager manager = Managers.getDefault();
    public Task task;
    public Task task2;
    public Epic epic;
    public Epic epic2;
    public Subtask subtask;
    public Subtask subtask2;
    public int idEpic;

    @BeforeEach
    public void beforeEach() {
        task = new Task("1", "Задача 1");
        manager.createTask(task);
        task2 = new Task("2", "Задача 2");
        manager.createTask(task2);
        epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        epic2 = new Epic("2", "Эпик 2");
        manager.createEpic(epic2);
        idEpic = epic.getId();

        subtask = new Subtask("1", "Сабтаск 1", idEpic);
        manager.createSubtask(subtask);
        subtask2 = new Subtask("2", "Сабтаск 2", idEpic);
        manager.createSubtask(subtask2);
    }

    @AfterEach
    public void afterEach() {
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
    }

    @Test
    public void shouldReturnTrueIfTaskMangerAddTaskAndFind() {
        assertEquals(task, manager.getTaskById(task.getId()));
        assertEquals(task2, manager.getTaskById(task2.getId()));
    }

    @Test
    public void shouldReturnTrueIfTaskMangerAddEpicAndFind() {
        ArrayList<Epic> epics = new ArrayList<>();
        epics.add(epic);
        epics.add(epic2);

        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertEquals(epic2, manager.getEpicById(epic2.getId()));
        assertEquals(epics, manager.getAllEpics());
    }

    @Test
    public void shouldReturnTrueIfTaskMangerAddSubtaskAndFind() {
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void shouldReturnTrueIfTaskMangerGetAllTaskAndRemoveAll() {
        final ArrayList<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        manager.removeAllTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldReturnTrueIfTaskMangerGetAllEpicAndRemoveAll() {
        final ArrayList<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        manager.removeAllEpics();
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void shouldReturnTrueIfTaskMangerGetAllSubtaskAndRemoveAll() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);
        subtasks.add(subtask2);

        assertEquals(subtasks, manager.getAllSubtaskOfEpic(idEpic));

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");

        manager.removeAllSubtasks();
        assertEquals(0, manager.getAllSubtasks().size());
    }
}
