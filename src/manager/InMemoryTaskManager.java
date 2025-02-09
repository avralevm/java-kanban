package manager;

import exception.TaskOverlapException;
import task.*;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected int countID = 0;
    protected HistoryManager historyManager;
    protected Set<Task> prioritizedTasks;

    InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    private int getCountID() {
        return countID++;
    }

    //Methods Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        prioritizedTasks.removeIf(task -> task.getTypeTask() == TypeTask.TASK);
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
        if (isOverlapping(task)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        task.setId(getCountID());
        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateTask(Task task) throws TaskOverlapException {
        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);

        if (isOverlapping(task)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void removeTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    //Methods Epic
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task.getTypeTask() == TypeTask.SUBTASK);
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
        if (isOverlapping(epic)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        epic.setId(getCountID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) throws TaskOverlapException {
        if (isOverlapping(epic)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        epics.put(epic.getId(), epic);
        epic.updateStatus();
        epic.updateTimeFields();
    }

    @Override
    public void removeEpic(int id) {
        List<Subtask> subtasksEpic = epics.get(id).getSubtasks();
        for (Subtask subtask : subtasksEpic) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getAllSubtaskOfEpic(int idEpic) {
        return subtasks.values().stream()
                .filter(subtask -> idEpic == subtask.getEpicId())
                .collect(Collectors.toList());
    }

    //Methods Subtask
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
            epic.updateTimeFields();
        }
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task.getTypeTask() == TypeTask.SUBTASK);
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
        if (isOverlapping(subtask)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        subtask.setId(getCountID());
        subtasks.put(subtask.getId(), subtask);

        // Update Epic Fields
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasks().add(subtask);
        epic.updateStatus();
        epic.updateTimeFields();

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TaskOverlapException {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(oldSubtask);

        if (isOverlapping(subtask)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.updateStatus();
        epic.updateTimeFields();
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());

        epic.getSubtasks().remove(subtask);
        subtasks.remove(id);
        historyManager.remove(id);
        epic.updateStatus();

        epic.updateTimeFields();
        prioritizedTasks.remove(subtask);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private boolean isOverlapping(Task validTask) {
        if (validTask.getStartTime() == null || validTask.getDuration() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(task -> task.getEndTime().isAfter(validTask.getStartTime()) &&
                                task.getStartTime().isBefore(validTask.getEndTime())
                );
    }
}