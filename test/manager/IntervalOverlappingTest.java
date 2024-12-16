package manager;

import exception.TaskOverlapException;
import org.junit.jupiter.api.Test;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntervalOverlappingTest {
    private TaskManager manager = new InMemoryTaskManager();

    @Test
    public void OverlappingTaskTest() {
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);
        Task task2 = new Task("2", "Задача 2", Duration.ofHours(2), LocalDateTime.now());

        Exception exception = assertThrows(TaskOverlapException.class, () -> {
            manager.createTask(task2);
        });
    }
}
