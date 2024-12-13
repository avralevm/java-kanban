import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task1);

        Task task2 = new Task("2", "Задача 2", Duration.ofMinutes(30),
                task1.getEndTime().plusHours(2));
        manager.createTask(task2);

        Epic epic = new Epic("3", "Эпик 3");
        manager.createEpic(epic);

        System.out.println(manager.getPrioritizedTasks());

        Epic epic2 = new Epic("4", "Эпик 4");
        manager.createEpic(epic2);

        System.out.println(manager.getPrioritizedTasks());

        Subtask subtask = new Subtask("5", "Сабтаск 5", epic.getId(), Duration.ofMinutes(15),
                task2.getEndTime().plusHours(1));
        manager.createSubtask(subtask);

        System.out.println(manager.getPrioritizedTasks());

        Subtask subtask2 = new Subtask("6", "Сабтаск 6", epic.getId(), Duration.ofMinutes(45),
                subtask.getEndTime().plusMinutes(15));
        manager.createSubtask(subtask2);

        System.out.println(manager.getPrioritizedTasks());

        Task task4 = new Task("1", "Задача 1", Duration.ofMinutes(30),
                LocalDateTime.of(2021, 12,6, 22, 36));
        manager.createTask(task4);

        Task task5 = new Task("2", "Задача 2", Duration.ofHours(1),
                LocalDateTime.of(2022, Month.DECEMBER, 6, 23 ,5));
        manager.createTask(task5);

        Epic epic3 = new Epic("3", "Эпик 3");
        manager.createEpic(epic3);

        Subtask subtask1 = new Subtask("4", "Сабтаск 4", epic3.getId(), Duration.ofDays(30),
                LocalDateTime.of(2020,1,1,1,1));
        manager.createTask(subtask1);

        System.out.println(manager.getPrioritizedTasks());

        Task task = new Task("Задача 1", "Обновление Задачи 1", Duration.ofMinutes(45),
                LocalDateTime.of(2024,1,1,1,1));
        manager.updateTask(task);
        System.out.println(manager.getPrioritizedTasks());
    }
}