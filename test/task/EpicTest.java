package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    public static TaskManager manager = Managers.getDefault();

    @Test
    public void shouldReturnTrueIfIdEpic1EqualsIdEpic2() {
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        final Epic epic2 = manager.getEpicById(epic.getId());

        assertNotNull(epic2, "Задача не найдена.");
        assertEquals(epic, epic2, "ID Epic и Epic2 несовпадают");
    }


}