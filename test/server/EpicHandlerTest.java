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

public class EpicHandlerTest {
    HttpTaskServer server = new HttpTaskServer();
    TaskManager manager = server.getManager();
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public EpicHandlerTest() throws IOException {
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
    public void addEpicTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic = new Epic("Test Epic", "Эпик 1");
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = manager.getAllEpics();
        assertEquals(201, response.statusCode());
        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество задач");
        assertEquals("Test Epic", epics.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void updateEpicTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);

        Epic epicUpdate = new Epic("Epic 1 Update", "Epic Description Update");
        epicUpdate.setId(epic.getId());
        String epicUpdateJson = gson.toJson(epicUpdate);

        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicUpdateJson)).build();
        HttpResponse<String> response = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = manager.getAllEpics();
        assertEquals(201, response.statusCode());
        assertEquals(1, epics.size(), "Некорректное количество задач");
        assertEquals("Epic 1 Update", epics.get(0).getTitle(), "Некорректное имя задачи");
        assertEquals("Epic Description Update", epics.get(0).getDescription(), "Некорректное описание");
    }

    @Test
    public void getAllEpicsTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        Epic epic2 = new Epic("Epic 2", "Epic 2 Description");
        manager.createEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        Task responseEpic1 = gson.fromJson(jsonArray.get(0), Epic.class);
        Task responseEpic2 = gson.fromJson(jsonArray.get(1), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(2, jsonArray.size(), "Некорректное количество задач");
        assertEquals(epic, responseEpic1, "Задачи не совпадают");
        assertEquals(epic2, responseEpic2, "Задачи не совпадают");
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        Epic epicResponse = gson.fromJson(jsonObject, Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(0, epicResponse.getId(), "Id не совпадает");
        assertEquals(epic.getTitle(), epicResponse.getTitle(), "Заголовки не совпадают");
        assertEquals(epic.getDescription(), epicResponse.getDescription(), "Описания не совпадают");

        // Проверка на несуществующий ресурс
        URI url2 = URI.create("http://localhost:8080/epics/2");
        HttpRequest requestError = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> responseError = client.send(requestError, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseError.statusCode());
        assertEquals("Ресурс не найден", responseError.body().toString());
    }

    @Test
    public void getEpicSubtasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
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
        Subtask responseSubtask1 = gson.fromJson(jsonArray.get(0), Subtask.class);
        Subtask responseSubtask2 = gson.fromJson(jsonArray.get(1), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(2, jsonArray.size(), "Некорректное количество задач");
        assertEquals(subtask, responseSubtask1, "Задачи не совпадают");
        assertEquals(subtask2, responseSubtask2, "Задачи не совпадают");

        // Проверка на несуществующий ресурс
        URI url2 = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest requestError = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> responseError = client.send(requestError, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseError.statusCode());
        assertEquals("Ресурс не найден", responseError.body().toString());
    }

    @Test
    public void deleteEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask 1", epic.getId(), Duration.ofMinutes(15),
                LocalDateTime.of(2025,02,9,13,42,50));
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtaskOfEpic(epic.getId());
        assertEquals(200, response.statusCode());
        assertEquals(0, epics.size(), "Некорректное количество задач");
        assertEquals(0, subtasks.size(), "Некорректное количество задач");

        Epic epicResponse = manager.getEpicById(epic.getId());
        assertNull(epicResponse);
    }
}