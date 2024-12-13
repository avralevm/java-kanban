package manager;

import exception.TaskOverlapException;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
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
        tasks.put(task.getId(), task);

        prioritizedTasks.remove(oldTask);
        if (isOverlapping(task)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
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
        epics.put(epic.getId(), epic);
        updateStatus(epic);

        if (isOverlapping(epic)) {
            throw new TaskOverlapException("Время задач пересекается");
        }
    }

    @Override
    public void removeEpic(int id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id) {
                subtasks.remove(subtask);
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
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
            epic.getSubtasksId().clear();
            epic.setStatus(Status.NEW);
            updateEpicTimeFields(epic);
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
        epic.getSubtasksId().add(subtask.getId());
        updateStatus(epic);
        updateEpicTimeFields(epic);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TaskOverlapException {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        updateStatus(epic);
        updateEpicTimeFields(epic);

        prioritizedTasks.remove(oldSubtask);
        if (isOverlapping(subtask)) {
            throw new TaskOverlapException("Время задач пересекается");
        }

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());

        epic.getSubtasksId().remove(Integer.valueOf(id));
        subtasks.remove(id);
        historyManager.remove(id);
        updateStatus(epic);

        updateEpicTimeFields(epic);
        prioritizedTasks.remove(subtask);
    }

    private void updateStatus(Epic epic) {
        boolean statusDONE = true;
        boolean statusNEW = true;
        for (Integer subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != Status.NEW) {
                statusNEW = false;
            } else if (subtask.getStatus() != Status.DONE) {
                statusDONE = false;
            }
        }
        //StatusSolution
        if (statusDONE) {
            epic.setStatus(Status.DONE);
        } else if (statusNEW) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicTimeFields(Epic epic) {
        List<Subtask> subtasksEpic = getAllSubtaskOfEpic(epic.getId());
        Duration sumDurationSubtasks = subtasksEpic.stream()
                .filter(subtask -> subtask.getDuration() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(sumDurationSubtasks);

        LocalDateTime minStartTime = subtasksEpic.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Subtask::getStartTime))
                .map(Subtask::getStartTime)
                .orElse(null); // возможно нужно заменить на null
        epic.setStartTime(minStartTime);

        LocalDateTime maxEndTime = subtasksEpic.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(subtask -> subtask.getEndTime()))
                .map(Subtask::getEndTime)
                .orElse(null);
        epic.setEndTime(maxEndTime);
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}