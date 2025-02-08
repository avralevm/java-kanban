package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest {
    HttpTaskServer server = new HttpTaskServer();
    TaskManager manager = server.getManager();
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public SubtaskHandlerTest() throws IOException {
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
    public void addSubtaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("1", "Эпик 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask Description", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(201, response.statusCode());
        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество задач");
        assertEquals("Subtask", subtasks.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/1");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask 2", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        manager.createSubtask(subtask);

        Subtask subtaskUpdate = new Subtask("Subtask 2 Update", "Subtask 2 Update", epic.getId(), Duration.ofMinutes(45),
                subtask.getEndTime().plusMinutes(15));
        subtaskUpdate.setId(subtask.getId());
        subtaskUpdate.setStatus(Status.IN_PROGRESS);
        String subtaskUpdateJson = gson.toJson(subtaskUpdate);

        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskUpdateJson)).build();
        HttpResponse<String> response = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(201, response.statusCode());
        assertEquals(1, subtasks.size(), "Некорректное количество задач");
        assertEquals("Subtask 2 Update", subtasks.get(0).getTitle(), "Некорректное имя задачи");
        assertEquals("Subtask 2 Update", subtasks.get(0).getDescription(), "Некорректное описание");
        assertEquals(Status.IN_PROGRESS, subtasks.get(0).getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void overlapExceptionTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask 1", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        manager.createSubtask(subtask);

        Subtask subtaskIntersections = new Subtask("Subtask OverlapException", "Subtask OverlapException", epic.getId(), subtask.getDuration(),
                subtask.getStartTime());

        String subtaskIntersectionsJson = gson.toJson(subtaskIntersections);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskIntersectionsJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> tasks = manager.getAllSubtasks();
        assertEquals(406, response.statusCode());
        assertEquals("Конфликт с существующими задачами", response.body().toString());
        assertEquals(1, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void getAllSubtasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask 1", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2", epic.getId(), Duration.ofMinutes(45),
                subtask.getEndTime().plusMinutes(15));
        manager.createSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        Task responseSubtask1 = gson.fromJson(jsonArray.get(0), Subtask.class);
        Task responseSubtask2 = gson.fromJson(jsonArray.get(1), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(2, jsonArray.size(), "Некорректное количество задач");
        assertEquals(subtask, responseSubtask1, "Задачи не совпадают");
        assertEquals(subtask2, responseSubtask2, "Задачи не совпадают");
    }

    @Test
    public void getSubtaskByIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/1");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask 1", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        Subtask subtaskResponse = gson.fromJson(jsonObject, Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, subtaskResponse.getId(), "Id не совпадает");
        assertEquals(subtask.getTitle(), subtaskResponse.getTitle(), "Заголовки не совпадают");
        assertEquals(subtask.getDescription(), subtaskResponse.getDescription(), "Описания не совпадают");

        // Проверка на несуществующий ресурс
        URI url2 = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestError = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> responseError = client.send(requestError, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseError.statusCode());
        assertEquals("Ресурс не найден", responseError.body().toString());
    }

    @Test
    public void deleteSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/1");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask 1", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> tasks = manager.getAllSubtasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasks.size(), "Некорректное количество задач");
        Subtask subtaskResponse = manager.getSubtaskById(subtask.getId());
        assertNull(subtaskResponse);
    }
}