import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {
    public static void main(String[] args) {
    TaskManager manager = Managers.getDefault();

    createTasks(manager);
    printAllTasks(manager);
    System.out.println();

    manager.removeAllTasks();
    manager.removeAllEpics();
    manager.removeAllSubtasks();

    System.out.println();
    printAllTasks(manager);
    System.out.println();

    Task task5 = new Task("12", "Задача 4");
    manager.createTask(task5);


    Task task5Get= manager.getTaskById(task5.getId());
    System.out.println();
    printAllTasks(manager);
    System.out.println();

    Epic epic5 = new Epic("13", "Эпик 4");
    manager.createEpic(epic5);
    Epic epic5Get = manager.getEpicById(epic5.getId());

    System.out.println();
    printAllTasks(manager);
    System.out.println();

    Subtask subtask5 = new Subtask("14", "Подзадача 5");
    subtask5.setEpicId(epic5.getId());
    manager.createSubtask(subtask5);
    Subtask subtask5Get = manager.getSubtaskById(subtask5.getId());

    System.out.println();
    printAllTasks(manager);
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

    public static void createTasks(TaskManager manager) {
        Task task = new Task("1", "Задача 1");
        manager.createTask(task);
        Task task1Get = manager.getTaskById(task.getId());

        Epic epic = new Epic("2", "Эпик 1");
        manager.createEpic(epic);
        Epic epic1Get = manager.getEpicById(epic.getId());

        Subtask subtask = new Subtask("3", "Сабтаск 1");
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);
        Subtask subtask1Get = manager.getSubtaskById(subtask.getId());


        Task task2 = new Task("4", "Задача 2");
        manager.createTask(task2);
        Task task2Get = manager.getTaskById(task2.getId());

        Epic epic2 = new Epic("5", "Эпик 2");
        manager.createEpic(epic2);
        Epic epic2Get = manager.getEpicById(epic2.getId());

        Subtask subtask2 = new Subtask("6", "Подзадача 2");
        subtask2.setEpicId(epic2.getId());
        manager.createSubtask(subtask2);
        Subtask subtask2Get = manager.getSubtaskById(subtask2.getId());

        Task task3 = new Task("7", "Задача 3");
        manager.createTask(task3);
        Task task3Get = manager.getTaskById(task3.getId());

        Epic epic3 = new Epic("8", "Эпик 3");
        manager.createEpic(epic3);
        Epic epic3Get = manager.getEpicById(epic3.getId());

        Subtask subtask3 = new Subtask("8", "Подзадача 3");
        subtask3.setEpicId(epic3.getId());
        manager.createSubtask(subtask3);
        Subtask subtask3Retrieved = manager.getSubtaskById(subtask3.getId());

        Task task4 = new Task("9", "Задача 4");
        manager.createTask(task4);
        Task task4Retrieved = manager.getTaskById(task4.getId());

        Epic epic4 = new Epic("10", "Эпик 4");
        manager.createEpic(epic4);
        Epic epic4Get = manager.getEpicById(epic4.getId());

        Subtask subtask4 = new Subtask("11", "Подзадача 4");
        subtask4.setEpicId(epic4.getId());
        manager.createSubtask(subtask4);
        Subtask subtask4Get = manager.getSubtaskById(subtask4.getId());
    }

}
