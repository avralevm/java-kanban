package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager{
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int countID = 0;
    private HistoryManager historyManager;
    InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    //Methods tasks.Task
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }
    @Override
    public void createTask(Task task) {
        task.setId(getCountID());
        tasks.put(task.getId(), task);
    }
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    //Methods tasks.Epic
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }
    @Override
    public void createEpic(Epic epic) {
        epic.setId(getCountID());
        epics.put(epic.getId(), epic);
    }
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateStatus(epic);
    }
    @Override
    public void removeEpic(int id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtasks.remove(subtask);
            }
        }
        epics.remove(id);
    }
    @Override
    public ArrayList<Subtask> getAllSubtaskOfEpic(int idEpic) {
        ArrayList<Subtask> arrSubtasks = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (idEpic == subtask.getEpicId()) {
                arrSubtasks.add(subtask);
            }
        }
        return arrSubtasks;
    }

    //Methods tasks.Subtask
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            epic.setStatus(Status.NEW);
        }
        subtasks.clear();
    }
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }
    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(getCountID());
        subtasks.put(subtask.getId(), subtask);

        // Update ArrayList tasks.Subtask
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());

        updateStatus(epic);
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        updateStatus(epic);
    }
    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());

        epic.getSubtasksId().remove(Integer.valueOf(id));
        subtasks.remove(id);
        updateStatus(epic);
    }

    private void updateStatus(Epic epic) {
        boolean StatusDONE = true;
        boolean StatusNEW = true;
        for (Integer subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != Status.DONE) {
                StatusDONE = false;
            } else if (subtask.getStatus() != Status.NEW) {
                StatusNEW = false;
            }
        }
        //StatusSolution
        if (StatusDONE) {
            epic.setStatus(Status.DONE);
        } else if (StatusNEW) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
    private int getCountID() {
        return countID++;
    }

}
