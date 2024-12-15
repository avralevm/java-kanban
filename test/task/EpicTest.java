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

class EpicTest {
    public static TaskManager manager = Managers.getDefault();

    Epic epic;
    Subtask subtask, subtask2;

    @BeforeEach
    public void createTask() {
        epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);

        subtask = new Subtask("1", "Сабтаск 1", epic.getId(), Duration.ofMinutes(15)
                , LocalDateTime.of(2020,1,1,1,1));
        subtask2 = new Subtask("2", "Сабтаск 2", epic.getId(), Duration.ofMinutes(45),
                subtask.getEndTime().plusMinutes(15));
        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);
    }
    @AfterEach
    public void deleteAllTask() {
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
    }

    @Test
    public void epic1EqualsIdEpic2Test() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        final Epic epic2 = manager.getEpicById(epic.getId());

        assertNotNull(epic2, "Задача не найдена.");
        assertEquals(epic, epic2, "ID Epic и Epic2 несовпадают");
    }

    @Test
    public void epicStatusTest() {
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicStatusSubtaskStatusIN_PROGRESSTest() {
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }
}