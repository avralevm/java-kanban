package server.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskOverlapException;
import manager.TaskManager;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> getHandle(exchange);
                case "POST" ->  postHandle(exchange);
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
                List<Epic> epics = manager.getAllEpics();
                sendResponse(exchange, gson.toJson(epics), 200);
                break;
            }
            case 3: {
                Optional<Integer> idEpic = getId(exchange);
                Epic epic = manager.getEpicById(idEpic.get());
                checkIsExist(exchange, epic);
                break;
            }
            case 4: {
                Optional<Integer> idEpic = getId(exchange);
                if (manager.getEpicById(idEpic.get()) != null) {
                    List<Subtask> subtasks = manager.getAllSubtaskOfEpic(idEpic.get());
                    sendResponse(exchange, gson.toJson(subtasks), 200);;
                } else {
                    sendNotFound(exchange);
                }
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
        Epic epic = gson.fromJson(request, Epic.class);
        try {
            switch (splitEl.length) {
                case 2: {
                    manager.createEpic(epic);
                    sendResponse(exchange,  "Epic создан",201);
                    break;
                }
                case 3: {
                    manager.updateEpic(epic);
                    sendResponse(exchange,  "Epic обновлён",201);
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
            Optional<Integer> idEpic = getId(exchange);
            if (manager.getEpicById(idEpic.get()) != null) {
                manager.removeEpic(idEpic.get());
                sendResponse(exchange,"Epic удалён", 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendBadRequest(exchange);
        }
    }
}