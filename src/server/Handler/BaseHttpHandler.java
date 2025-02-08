package server.Handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.Managers;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    protected TaskManager manager;
    protected Gson gson;

    public BaseHttpHandler (TaskManager manager) {
        this.manager = manager;
        gson = Managers.getGson();
    }

    protected Optional<Integer> getId (HttpExchange exchange) throws IOException {
        String[] splitEl = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(splitEl[2]));
        } catch (NumberFormatException exception) {
            sendBadRequest(exchange, "Получен некорректный id = " + splitEl[2]);
            return Optional.empty();
        }
    }

    protected void checkIsExist(HttpExchange exchange, Task task) throws IOException {
        if (task != null) {
            sendResponse(exchange, gson.toJson(task), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    protected void sendResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        byte[] responseBytes = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Ресурс не найден", 404);
    }

    protected void sendHasIntersections(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Конфликт с существующими задачами", 406);
    }

    protected void sendServerError(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Ошибка сервера", 500);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Получен некорректный запрос", 400);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, message, 400);
    }
}