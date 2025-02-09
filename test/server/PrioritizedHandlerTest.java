package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {
    HttpTaskServer server = new HttpTaskServer();
    TaskManager manager = server.getManager();
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public PrioritizedHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("1", "Задача 1", Duration.ofHours(1), LocalDateTime.now());
        manager.createTask(task);
        Task task2 = new Task("2", "Задача 2", Duration.ofHours(1), task.getEndTime().plusHours(1));
        manager.createTask(task2);
        Epic epic = new Epic("3", "Эпик 3");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("4", "Сабтаск 4", epic.getId(), Duration.ofHours(1), task2.getEndTime().plusHours(1));
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("5", "Сабтаск 5", epic.getId(), Duration.ofHours(1), subtask.getEndTime().plusHours(1));
        manager.createSubtask(subtask2);

        URI url= URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(4, jsonArray.size(), "Некорректное количество задач");

        Task actualTask = gson.fromJson(jsonArray.get(0), Task.class);
        assertEquals(task, actualTask, "Задачи не совпадают");

        Task actualTask2 = gson.fromJson(jsonArray.get(1), Task.class);
        assertEquals(task2, actualTask2, "Задачи не совпадают");

        Subtask actualSubtask = gson.fromJson(jsonArray.get(2), Subtask.class);
        assertEquals(subtask, actualSubtask, "Задачи не совпадают");

        Subtask actualSubtask2 = gson.fromJson(jsonArray.get(3), Subtask.class);
        assertEquals(subtask2, actualSubtask2, "Задачи не совпадают");
    }
}