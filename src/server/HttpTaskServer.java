package server;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.Handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

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
            taskServer.start();
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