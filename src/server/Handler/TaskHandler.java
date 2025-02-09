package server.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskOverlapException;
import manager.TaskManager;

import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> getHandle(exchange);
                case "POST" -> postHandle(exchange);
                case "DELETE" -> deleteHandle(exchange);
                default -> sendResponse(exchange, "Такого запроса нету: " + method, 405);
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    private void getHandle(HttpExchange exchange) throws IOException {
        String[] splitEl = exchange.getRequestURI().getPath().split("/");

        switch (splitEl.length) {
            case 2: {
                List<Task> tasks = manager.getAllTasks();
                sendResponse(exchange, gson.toJson(tasks), 200);
                break;
            }
            case 3: {
                Optional<Integer> idTask = getId(exchange);
                Task task = manager.getTaskById(idTask.get());
                checkIsExist(exchange, task);
                break;
            }
            default:
                sendBadRequest(exchange);
        }
    }

    private void postHandle(HttpExchange exchange) throws IOException {
        String[] splitEl = exchange.getRequestURI().getPath().split("/");

        InputStream is = exchange.getRequestBody();
        String request = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(request, Task.class);
        try {
            switch (splitEl.length) {
                case 2: {
                    manager.createTask(task);
                    sendResponse(exchange,  "Task создан",201);
                    break;
                }
                case 3: {
                    manager.updateTask(task);
                    sendResponse(exchange,  "Task обновлён",201);
                    break;
                }
                default:
                    sendBadRequest(exchange);
            }
        } catch (TaskOverlapException exception) {
            sendHasIntersections(exchange);
        }
    }

    private void deleteHandle(HttpExchange exchange) throws IOException {
        String[] splitEl = exchange.getRequestURI().getPath().split("/");
        if (splitEl.length == 3) {
            Optional<Integer> idTask = getId(exchange);
            if (manager.getTaskById(idTask.get()) != null) {
                manager.removeTask(idTask.get());
                sendResponse(exchange,"Task удалён", 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendBadRequest(exchange);
        }
    }
}