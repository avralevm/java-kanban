import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private static HashMap<Integer, Task> tasks;
    private static HashMap<Integer, Epic> epics;
    private static HashMap<Integer, Subtask> subtasks;
    private static int countID = 0;

    TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }
    public static HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public static HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
    public static int getCountID() {
        return countID;
    }

    //MethodsTask
    public static ArrayList<Task> getAllTasks() {
        ArrayList<Task> arrTasks = new ArrayList<>();

        for (Task task : tasks.values()) {
            arrTasks.add(task);
        }
        return arrTasks;
    }
    public static void removeAllTask() {
        tasks.clear();
    }
    public static Task getTaskById(int id) {
        return tasks.get(id);
    }
    public static void createTask(Task task) {
        task.setId(countID++);
        tasks.put(task.getId(), task);
    }
    public static void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public static void removeTask(int id) {
        tasks.remove(id);
    }

    //MethodsEpic
    public static ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> arrEpics = new ArrayList<>();

        for (Epic epic : epics.values()) {
            arrEpics.add(epic);
        }
        return arrEpics;
    }
    public static void removeAllEpics() {
        epics.clear();
    }
    public static Epic getEpicById(int id) {
        return epics.get(id);
    }
    public static void createEpic(Epic epic) {
        epic.setId(countID++);
        epics.put(epic.getId(), epic);
    }
    public static void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        //UpdateStatusEpic
        int countStatusDONE = 0;
        int countStatusNEW = 0;
        for (Integer subtaskId : epic.subtasksId) {
            Subtask anSubtask = subtasks.get(subtaskId);
            if (anSubtask.getStatus() == Status.DONE) {
                countStatusDONE++;
            } else if (anSubtask.getStatus() == Status.NEW) {
                countStatusNEW++;
            }
        }
        //StatusSolution
        if (countStatusDONE == epic.subtasksId.size()) {
            epic.setStatus(Status.DONE);
        } else if (countStatusNEW == epic.subtasksId.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
    public static void removeEpic(int id) {
        epics.remove(id);
    }
    public static ArrayList<Subtask> getAllSubtaskOfEpic(Epic epic) {
        ArrayList<Subtask> arrSubtasks = new ArrayList<>();

        int epicId= epic.getId();
        for (Subtask subtask : subtasks.values()) {
            if (epicId == subtask.getEpicId()) {
                arrSubtasks.add(subtask);
            }
        }
        return arrSubtasks;
    }

    //MethodsSubtask
    public static ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> arrSubtasks = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            arrSubtasks.add(subtask);
        }
        return arrSubtasks;
    }
    public static void removeAllSubtasks() {
        subtasks.clear();
    }
    public static Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }
    public static void createSubtask(Subtask subtask) {
        subtask.setId(countID++);
        subtasks.put(subtask.getId(), subtask);

        // Update ArrayList Subtask
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());

        //UpdateStatusEpic
        int countStatusDONE = 0;
        int countStatusNEW = 0;
        for (Integer subtaskId : epic.subtasksId) {
            Subtask anSubtask = subtasks.get(subtaskId);
            if (anSubtask.getStatus() == Status.DONE) {
                countStatusDONE++;
            } else if (anSubtask.getStatus() == Status.NEW) {
                countStatusNEW++;
            }
        }
        //StatusSolution
        if (countStatusDONE == epic.subtasksId.size()) {
            epic.setStatus(Status.DONE);
        } else if (countStatusNEW == epic.subtasksId.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
    public static void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());

        //UpdateStatusEpic
        int countStatusDONE = 0;
        int countStatusNEW = 0;
        for (Integer subtaskId : epic.subtasksId) {
            Subtask anSubtask = subtasks.get(subtaskId);
            if (anSubtask.getStatus() == Status.DONE) {
                countStatusDONE++;
            } else if (anSubtask.getStatus() == Status.NEW) {
                countStatusNEW++;
            }
        }
        //StatusSolution
        if (countStatusDONE == epic.subtasksId.size()) {
            epic.setStatus(Status.DONE);
        } else if (countStatusNEW == epic.subtasksId.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
    public static void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id); // Получение Subtask по id
        Epic epic = epics.get(subtask.getEpicId()); //Получение какому Epic принадлежит Subtask

        epic.getSubtasksId().remove(Integer.valueOf(id));
        subtasks.remove(id);
    }
}
