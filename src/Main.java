import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {
    public static void main(String[] args) {
    TaskManager manager = Managers.getDefault();

    Task task = new Task("1", "Задача 1");
    manager.createTask(task);

    Task task2 = new Task("2", "Задача 2");
    manager.createTask(task2);

    Epic epic = new Epic("3", "Эпик 1");
    manager.createEpic(epic);

    Subtask subtask = new Subtask("4", "Сабтаск 1");
    subtask.setEpicId(epic.getId());
    manager.createSubtask(subtask);

    Subtask subtask2 = new Subtask("5", "Сабтаск 2");
    subtask2.setEpicId(epic.getId());
    manager.createSubtask(subtask2);

    Subtask subtask3 = new Subtask("6", "Сабтаск 3");
    subtask3.setEpicId(epic.getId());
    manager.createSubtask(subtask3);

    Epic epic2 = new Epic("7", "Эпик 2");
    manager.createEpic(epic2);


    printAllTasks(manager);

    System.out.println("\n" + "История просмотров задач");
    //Проверка на повтор
    manager.getTaskById(task.getId());
    manager.getEpicById(epic.getId());
    printHistory(manager);

    manager.getEpicById(epic.getId());
    manager.getEpicById(epic2.getId());
    printHistory(manager);

    manager.getSubtaskById(subtask.getId());
    manager.getSubtaskById(subtask2.getId());
    manager.getSubtaskById(subtask3.getId());
    manager.getTaskById(task.getId());
    printHistory(manager);

    manager.getSubtaskById(subtask.getId());
    printHistory(manager);

    System.out.println("\n" + "История просмотров задач при удалении");
    manager.removeTask(task.getId());
    printHistory(manager);

    manager.removeTask(subtask.getId());
    printHistory(manager);

    manager.removeEpic(epic.getId());
    printHistory(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubtaskOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void printHistory(TaskManager manager) {
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}
