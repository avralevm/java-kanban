package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.sql.SQLOutput;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    public static TaskManager manager = Managers.getDefault();
    public static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldReturnTrueIfManagerReturnReadyHistoryManager() {
        Task task = new Task("1", "Задача 1");
        Task task2 = new Task("2", "Задача 2");
        historyManager.add(task);
        historyManager.add(task2);

        assertNotNull(historyManager.getHistory(), "История не возвращаются.");
        assertEquals(2, historyManager.getHistory().size(), "Невероное колличество задач");
        assertEquals(task, historyManager.getHistory().get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldReturnTrueWhenUpdateTask() {
        Task task = new Task("1", "Задача 1");
        manager.createTask(task);
        historyManager.add(task);

        Task taskUpdate = new Task("update_1", "Update задачи 1");
        taskUpdate.setId(task.getId());
        manager.updateTask(taskUpdate);
        assertEquals(task, taskUpdate);

        historyManager.add(taskUpdate);
        assertEquals(task, historyManager.getHistory().get(0));
    }


}