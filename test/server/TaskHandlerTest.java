package server;
import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
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

public class TaskHandlerTest {
    HttpTaskServer server = new HttpTaskServer();
    TaskManager manager = server.getManager();
    Gson gson = Managers.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public TaskHandlerTest() throws IOException {
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
    public void addTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Test task", "Testing task", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(201, response.statusCode());
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test task", tasks.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/1");
        Task task = new Task("Test task", "Testing task", Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);
        Task task2 = new Task("2", "Задача 2", Duration.ofMinutes(30), task.getEndTime().plusHours(2));
        manager.createTask(task2);

        Task taskUpdate = new Task("Task 2 Update", "Testing task 2 Update", Duration.ofMinutes(10), task.getEndTime().plusHours(4));
        taskUpdate.setId(task2.getId());
        taskUpdate.setStatus(Status.IN_PROGRESS);
        String taskUpdateJson = gson.toJson(taskUpdate);

        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskUpdateJson)).build();
        HttpResponse<String> response = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(201, response.statusCode());
        assertEquals(2, tasks.size(), "Некорректное количество задач");
        assertEquals("Task 2 Update", tasks.get(1).getTitle(), "Некорректное имя задачи");
        assertEquals("Testing task 2 Update", tasks.get(1).getDescription(), "Некорректное описание");
        assertEquals(Status.IN_PROGRESS, tasks.get(1).getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void overlapExceptionTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Test task", "Testing task", Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        Task taskIntersections = new Task("Task OverlapException", "Task OverlapException", task.getDuration(), task.getStartTime());
        String taskIntersectionsJson = gson.toJson(taskIntersections);

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskIntersectionsJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(406, response.statusCode());
        assertEquals("Конфликт с существующими задачами", response.body().toString());
        assertEquals(1, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);
        Task task2 = new Task("2", "Задача 2", Duration.ofMinutes(30), task.getEndTime().plusHours(2));
        manager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        Task responseTask1 = gson.fromJson(jsonArray.get(0), Task.class);
        Task responseTask2 = gson.fromJson(jsonArray.get(1), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(2, jsonArray.size(), "Некорректное количество задач");
        assertEquals(task, responseTask1, "Задачи не совпадают");
        assertEquals(task2, responseTask2, "Задачи не совпадают");
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        Task taskResponse = gson.fromJson(jsonObject, Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(0, taskResponse.getId(), "Id не совпадает");
        assertEquals(task.getTitle(), taskResponse.getTitle(), "Заголовки не совпадают");
        assertEquals(task.getDescription(), taskResponse.getDescription(), "Описания не совпадают");

        // Проверка на несуществующий ресурс
        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestError = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> responseError = client.send(requestError, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseError.statusCode());
        assertEquals("Ресурс не найден", responseError.body().toString());
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");
        Task task = new Task("1", "Задача 1", Duration.ofHours(2), LocalDateTime.now());
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasks.size(), "Некорректное количество задач");
        Task taskResponse = manager.getTaskById(task.getId());
        assertNull(taskResponse);
    }
}