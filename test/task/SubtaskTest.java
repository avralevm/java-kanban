package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskTest {
    public static TaskManager manager = Managers.getDefault();
    Epic epic;
    @BeforeEach
    private void createEpic() {
        epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
    }

    @AfterEach
    private void deleteEpic() {
        manager.removeEpic(epic.getId());
    }

    @Test
    public void subtask1EqualsIdSubtask2Test() {
        Subtask subtask = new Subtask("1", "Сабтаск 1", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 12, 30, 23, 15));
        manager.createSubtask(subtask);
        final Subtask subtask2 = manager.getSubtaskById(subtask.getId());

        assertNotNull(subtask2, "Задача не найдена.");
        assertEquals(subtask, subtask2, "ID Subtask и Subtask2 несовпадают");
    }

    @Test
    public void epicExistForSubtasksTest() {
        Subtask subtask = new Subtask("1", "Сабтаск 1", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2024, 12, 30, 23, 15));
        manager.createSubtask(subtask);

        assertEquals(epic.getId(), subtask.getEpicId(), "Subtask не знает об Epic");
    }
}