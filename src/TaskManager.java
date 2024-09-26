import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int countID = 0;

    TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Methods Task
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public void removeAllTask() {
        tasks.clear();
    }
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public void createTask(Task task) {
        task.setId(getCountID());
        tasks.put(task.getId(), task);
    }
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void removeTask(int id) {
        tasks.remove(id);
    }

    //Methods Epic
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }
    public void createEpic(Epic epic) {
        epic.setId(getCountID());
        epics.put(epic.getId(), epic);
    }
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateStatus(epic);
    }
    public void removeEpic(int id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtasks.remove(subtask);
            }
        }
        epics.remove(id);
    }
    public ArrayList<Subtask> getAllSubtaskOfEpic(Epic epic) {
        ArrayList<Subtask> arrSubtasks = new ArrayList<>();

        int epicId= epic.getId();
        for (Subtask subtask : subtasks.values()) {
            if (epicId == subtask.getEpicId()) {
                arrSubtasks.add(subtask);
            }
        }
        return arrSubtasks;
    }

    //Methods Subtask
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
        }
        subtasks.clear();
    }
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }
    public void createSubtask(Subtask subtask) {
        subtask.setId(getCountID());
        subtasks.put(subtask.getId(), subtask);

        // Update ArrayList Subtask
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());

        updateStatus(epic);
    }
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        updateStatus(epic);
    }
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id); // Получение Subtask по id
        Epic epic = epics.get(subtask.getEpicId()); //Получение какому Epic принадлежит Subtask

        epic.getSubtasksId().remove(Integer.valueOf(id));
        subtasks.remove(id);
        updateStatus(epic);
    }

    private void updateStatus(Epic epic) { //Переделал updateStatus на boolean
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

    private int getCountID() {
        return countID++;
    }
}
