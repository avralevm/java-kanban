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

public class HistoryHandlerTest {
    HttpTaskServer server = new HttpTaskServer();
    TaskManager manager = server.getManager();
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HistoryHandlerTest() throws IOException {
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
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);
        Epic epic = new Epic("3", "Эпик 3");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("5", "Сабтаск 5", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        manager.createSubtask(subtask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        URI url= URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertEquals(3, jsonArray.size(), "Некорректное количество задач");

        Task actualTask = gson.fromJson(jsonArray.get(0), Task.class);
        assertEquals(task, actualTask, "Задачи не совпадают");

        Epic actualEpic = gson.fromJson(jsonArray.get(1), Epic.class);
        assertEquals(epic, actualEpic, "Задачи не совпадают");

        Subtask actualSubtask = gson.fromJson(jsonArray.get(2), Subtask.class);
        assertEquals(subtask, actualSubtask, "Задачи не совпадают");
    }
}