package manager;

import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static HistoryManager historyManager;
    private static Task task, task2, task3;
    private static Epic epic;
    private static Subtask subtask;
    private int id = 0;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("1", "Задача 1");
        task.setId(id++);
        task2 = new Task("2", "Задача 2");
        task2.setId(id++);

        epic = new Epic("1", "Эпик 1");
        epic.setId(id++);

        subtask = new Subtask("1", "Сабтаск 1");
        subtask.setId(id++);
        subtask.setEpicId(epic.getId());
    }

    @Test
    public void ShouldReturnTrueIfCorrectAddLastTask() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(4, historyManager.getHistory().size(), "Несовпадает размер массива");

        assertEquals(task2, historyManager.getHistory().get(1), "Задача некорректно добавлется");
        assertEquals(epic, historyManager.getHistory().get(2),"Задачи разных типов некорректно добаляются");
        assertEquals(subtask, historyManager.getHistory().get(3),"Задачи разных типов некорректно добаляются");

        ArrayList<Task> testArr = new ArrayList<>(List.of(task, task2, epic, subtask));

        assertEquals(testArr, historyManager.getHistory(), "Хранятся в неправильном порядке");
    }

    @Test
    public void ShouldReturnTrueIfCorrectRemoveTask() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        //Удаление первой задачи
        historyManager.remove(task.getId());
        assertEquals(2,historyManager.getHistory().size(), "Начальная задача не удаляется");
        ArrayList<Task> testArr = new ArrayList<>(List.of(epic, subtask));
        assertEquals(testArr, historyManager.getHistory(), "Задачи не совпадают");

        //Удаление последней задачи
        historyManager.remove(subtask.getId());
        assertEquals(1,historyManager.getHistory().size(), "Последняя задача не удаляется");
        ArrayList<Task> testArr2 = new ArrayList<>(List.of(epic));
        assertEquals(testArr2, historyManager.getHistory(), "Задачи не совпадают");

        historyManager.remove(epic.getId());
        assertEquals(0, historyManager.getHistory().size(), "Задачи не удаляются");
    }

    @Test
    public void shouldReturnTrueWhenUpdateTaskInHistoryViewedNotUpdate() {
        historyManager.add(task);

        Task taskUpdate = new Task("update_1", "Update задачи 1");
        taskUpdate.setId(task.getId());
        historyManager.add(taskUpdate);

        assertEquals(task, taskUpdate, "Задачи не идентичны, при едином id");

        assertEquals(task, historyManager.getHistory().get(0)); /*Не понятно как проверить, что HistoryViewed хранит
        неизмённую задачу, ведь при сравнении через equals task и updateTask являются одной задачей*/

        historyManager.add(taskUpdate);
        assertEquals(1, historyManager.getHistory().size(), "Не перезаписывает изменнёную задачу");
    }

    @Test
    public void ShouldReturnTrueIfNotRepeatedViews() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size(), "Не перезаписываются задачи в historyManager");
    }
}