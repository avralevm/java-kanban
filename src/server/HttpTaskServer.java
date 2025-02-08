package server;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.Handler.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final  int PORT = 8080;
    private static HttpServer server;
    private static TaskManager manager;

    public HttpTaskServer() throws IOException {
        manager = Managers.getDefault();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer taskServer = new HttpTaskServer();
            Task task1 = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
            manager.createTask(task1);
            Task task2 = new Task("2", "Задача 2", Duration.ofMinutes(30),
                    task1.getEndTime().plusHours(2));
            manager.createTask(task2);
            Epic epic = new Epic("3", "Эпик 3");
            manager.createEpic(epic);

            Epic epic2 = new Epic("4", "Эпик 4");
            manager.createEpic(epic2);
            Subtask subtask = new Subtask("5", "Сабтаск 5", epic.getId(), Duration.ofMinutes(15),
                    LocalDateTime.of(2025,02,9,13,42,50));
            manager.createSubtask(subtask);

            Subtask subtask2 = new Subtask("6", "Сабтаск 6", epic.getId(), Duration.ofMinutes(45),
                    subtask.getEndTime().plusMinutes(15));
            manager.createSubtask(subtask2);
            taskServer.start();
            //taskServer.stop();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public TaskManager getManager() {
        return manager;
    }

    public void start() {
        System.out.println("Старт сервера");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }
}