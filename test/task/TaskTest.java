package task;

import manager.Managers;
import manager.TaskManager;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TaskTest {
    public static TaskManager manager = Managers.getDefault();


    @Test
    public void shouldReturnTrueIfIdTask1EqualsIdTask2() {
        Task task = new Task("1", "Задача 1");
        manager.createTask(task);
        final Task task2 = manager.getTaskById(task.getId());

        assertNotNull(task2, "Задача не найдена.");
        assertEquals(task, task2, "ID Task и Task2 несовпадают");
    }

}