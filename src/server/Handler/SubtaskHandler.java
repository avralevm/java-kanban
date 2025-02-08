package server.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskOverlapException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubtaskHandler(TaskManager manager) {
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
            e.getStackTrace();
            sendServerError(exchange);
        }
    }

    private void getHandle(HttpExchange exchange) throws IOException {
        String[] splitEl = exchange.getRequestURI().getPath().split("/");

        switch (splitEl.length) {
            case 2: {
                List<Subtask> subtasks = manager.getAllSubtasks();
                sendResponse(exchange, gson.toJson(subtasks), 200);
                break;
            }
            case 3: {
                Optional<Integer> idSubtask = getId(exchange);
                Subtask subtask = manager.getSubtaskById(idSubtask.get());
                checkIsExist(exchange, subtask);
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
        Subtask subtask = gson.fromJson(request, Subtask.class);
        try {
            switch (splitEl.length) {
                case 2: {
                    manager.createSubtask(subtask);
                    sendResponse(exchange,  "Subtask создан",201);
                    break;
                }
                case 3: {
                    manager.updateSubtask(subtask);
                    sendResponse(exchange,  "Subtask обновлён",201);
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
            Optional<Integer> idSubtask = getId(exchange);
            if (manager.getSubtaskById(idSubtask.get()) != null) {
                manager.removeSubtask(idSubtask.get());
                sendResponse(exchange,"Subtask удалён", 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendBadRequest(exchange);
        }
    }
}