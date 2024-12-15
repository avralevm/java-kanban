package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    public void setUp() throws IOException {
        manager = createTaskManager();
    }

    @Test
    public void createTaskTest() {
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);

        assertEquals(task, manager.getTaskById(task.getId()), "Task не создаётся");
    }

    @Test
    public void removeTaskTest() {
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);

        manager.removeTask(task.getId());
        assertEquals(0, manager.getAllTasks().size(), "Не удаляется Task");
    }

    @Test
    public void getAllTasksTest() {
        Task task1 = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("2", "Задача 2", Duration.ofHours(1), task1.getEndTime().plusHours(1));
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size(), "Должно быть 2 задачи");
        assertTrue(tasks.contains(task1), "Список задач должен содержать task1");
        assertTrue(tasks.contains(task2), "Список задач должен содержать task2");
    }

    @Test
    public void removeAllTasksTest() {
        Task task1 = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("2", "Задача 2", Duration.ofHours(1), task1.getEndTime().plusHours(1));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.removeAllTasks();

        assertEquals(0, manager.getAllTasks().size(), "Все задачи должны быть удалены");
    }

    @Test
    public void getTaskByIdTest() {
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);

        Task retrievedTask = manager.getTaskById(task.getId());
        assertEquals(task, retrievedTask, "Задача должна быть получена по ID");
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);

        Task updatedTask = new Task("1", "Обновленная задача", Duration.ofHours(3), LocalDateTime.now());
        updatedTask.setId(task.getId());
        manager.updateTask(updatedTask);

        assertEquals(updatedTask, manager.getTaskById(task.getId()), "Задача должна быть обновлена");
    }

    @Test
    public void createEpicTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        assertEquals(epic, manager.getEpicById(epic.getId()), "Epic не создаётся");
    }

    @Test
    public void removeEpicTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        manager.removeEpic(epic.getId());
        assertEquals(0, manager.getAllEpics().size(), "Не удаляется Epic");
    }

    @Test
    public void getAllEpicsTest() {
        Epic epic1 = new Epic("1", "Эпик 1");
        Epic epic2 = new Epic("2", "Эпик 2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size(), "Должно быть 2 эпика");
        assertTrue(epics.contains(epic1), "Список эпиков должен содержать epic1");
        assertTrue(epics.contains(epic2), "Список эпиков должен содержать epic2");
    }

    @Test
    public void removeAllEpicsTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        manager.removeAllEpics();

        assertEquals(0, manager.getAllEpics().size(), "Все эпики должны быть удалены");
    }

    @Test
    public void getEpicByIdTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        Epic retrievedEpic = manager.getEpicById(epic.getId());
        assertEquals(epic, retrievedEpic, "Эпик должен быть получен по ID");
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        Epic updatedEpic = new Epic("1", "Обновленный эпик");
        updatedEpic.setId(epic.getId());
        manager.updateEpic(updatedEpic);

        assertEquals(updatedEpic, manager.getEpicById(epic.getId()), "Эпик должен быть обновлен");
    }

    @Test
    public void createSubtaskTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("2", "Сабтаск 1", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.now());
        manager.createSubtask(subtask);

        assertEquals(subtask, manager.getSubtaskById(subtask.getId()), "Subtask не создаётся");
    }

    @Test
    public void removeSubtaskTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("2", "Сабтаск 1", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.now());
        manager.createSubtask(subtask);

        manager.removeSubtask(subtask.getId());
        assertEquals(0, manager.getAllSubtasks().size(), "Не удаляется Subtask");
    }

    @Test
    public void getAllSubtaskOfEpicTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("1", "Подзадача 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("2", "Подзадача 2", epic.getId(), Duration.ofMinutes(60), subtask1.getEndTime().plusHours(1));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        List<Subtask> subtasks = manager.getAllSubtaskOfEpic(epic.getId());
        assertEquals(2, subtasks.size(), "Должно быть 2 подзадачи для указанного эпика");
        assertTrue(subtasks.contains(subtask1), "Список подзадач должен содержать subtask1");
        assertTrue(subtasks.contains(subtask2), "Список подзадач должен содержать subtask2");
    }

    @Test
    public void getAllSubtasksTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("1", "Подзадача 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("2", "Подзадача 2", epic.getId(), Duration.ofMinutes(60), subtask1.getEndTime().plusHours(1));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(2, subtasks.size(), "Должно быть 2 подзадачи");
        assertTrue(subtasks.contains(subtask1), "Список подзадач должен содержать subtask1");
        assertTrue(subtasks.contains(subtask2), "Список подзадач должен содержать subtask2");
    }

    @Test
    public void removeAllSubtasksTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("1", "Подзадача 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("2", "Подзадача 2", epic.getId(), Duration.ofMinutes(60), subtask1.getEndTime().plusHours(1));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.removeAllSubtasks();

        assertEquals(0, manager.getAllSubtasks().size(), "Все подзадачи должны быть удалены");
    }

    @Test
    public void getSubtaskByIdTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("1", "Подзадача 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        manager.createSubtask(subtask);

        Subtask retrievedSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals(subtask, retrievedSubtask, "Подзадача должна быть получена по ID");
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("1", "Подзадача 1", epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        manager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask("1", "Обновленная подзадача", epic.getId(), Duration.ofDays(12), LocalDateTime.now());
        updatedSubtask.setId(subtask.getId());
        manager.updateSubtask(updatedSubtask);

        assertEquals(updatedSubtask, manager.getSubtaskById(subtask.getId()), "Подзадача должна быть обновлена");
    }

    @Test
    public void getHistoryTest() {
        Task task1 = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("2", "Задача 2", Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");
    }

    @Test
    public void getPrioritizedTasksTest() {
        Task task1 = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("2", "Задача 2", Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Должно быть 2 задачи");
        assertTrue(prioritizedTasks.contains(task1), "Список должен содержать task1");
        assertTrue(prioritizedTasks.contains(task2), "Список должен содержать task2");
    }
}
