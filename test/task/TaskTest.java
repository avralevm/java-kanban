package task;

import manager.Managers;
import manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TaskTest {
    public static TaskManager manager = Managers.getDefault();
    @Test
    public void task1EqualsIdTask2Test() {
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);
        final Task task2 = manager.getTaskById(task.getId());

        assertNotNull(task2, "Задача не найдена.");
        assertEquals(task, task2, "ID Task и Task2 несовпадают");
    }
}