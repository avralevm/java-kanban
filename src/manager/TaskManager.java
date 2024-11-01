package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public interface TaskManager {
    //tasks.Task
    ArrayList<Task> getAllTasks();
    void removeAllTasks();
    Task getTaskById(int id);
    void createTask(Task task);
    void updateTask(Task task);
    void removeTask(int id);

    //tasks.Epic
    ArrayList<Epic> getAllEpics();
    void removeAllEpics();
    Epic getEpicById(int id);
    void createEpic(Epic epic);
    void updateEpic(Epic Epic);
    void removeEpic(int id);
    ArrayList<Subtask> getAllSubtaskOfEpic(int id);

    //tasks.Subtask
    ArrayList<Subtask> getAllSubtasks();
    void removeAllSubtasks();
    Subtask getSubtaskById(int id);
    void createSubtask(Subtask subtask);
    void updateSubtask(Subtask subtask);
    void removeSubtask(int id);
    ArrayList<Task> getHistory();
}
