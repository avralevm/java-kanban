package manager;

import task.Task;

import java.util.ArrayList;
public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<Task> historyViewedTasks;
    InMemoryHistoryManager() {
        historyViewedTasks = new ArrayList<>();
    }
    @Override
    public void add(Task task) {
        if (historyViewedTasks.size() == 10) {
            historyViewedTasks.remove(0);
        }
        historyViewedTasks.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyViewedTasks;
    }
}
