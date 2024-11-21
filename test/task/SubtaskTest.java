package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskTest {
    public static TaskManager manager = Managers.getDefault();

    @Test
    public void shouldReturnTrueIfIdSubtask1EqualsIdSubtask2() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("2", "Сабтаск 1", epic.getId());
        manager.createSubtask(subtask);
        final Subtask subtask2 = manager.getSubtaskById(subtask.getId());

        assertNotNull(subtask2, "Задача не найдена.");
        assertEquals(subtask, subtask2, "ID Subtask и Subtask2 несовпадают");
    }
}