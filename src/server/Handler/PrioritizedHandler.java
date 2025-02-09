package server.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (method.equals("GET")) {
                getHandle(exchange);
            } else {
                sendResponse(exchange, "Такого запроса нету: " + method, 405);
            }
        } catch (Exception e) {
            e.getStackTrace();
            sendServerError(exchange);
        }
    }

    private void getHandle(HttpExchange exchange) throws IOException {
        String[] splitEl = exchange.getRequestURI().getPath().split("/");
        if (splitEl.length == 2) {
            List<Task> prioritizeTask = manager.getPrioritizedTasks();
            sendResponse(exchange,  gson.toJson(prioritizeTask),200);
        } else {
            sendBadRequest(exchange);
        }
    }
}