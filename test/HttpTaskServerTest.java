import com.google.gson.Gson;
import exception.NotFoundException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.*;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private static HttpClient client;
    private static URI url;
    private static TaskManager manager = new InMemoryTaskManager();
    private static HttpTaskServer server;
    private static Gson gson = new Gson();
    private static Task firstTask;
    private static Task secondTask;
    private static Epic firstEpic;
    private static Epic secondEpic;
    private static Subtask firstSubtask;
    private static Subtask secondSubtask;
    private static HttpRequest request;
    private static HttpResponse<String> response;

    @BeforeAll
    public static void beforeAll() throws IOException {
        server = new HttpTaskServer(manager);
        gson = server.getGson();
        client = HttpClient.newHttpClient();
        server.start();
    }

    @BeforeEach
    public void beforeEach() {
        manager.removeTasks();
        manager.removeEpics();
        manager.removeSubtasks();
        firstTask = new Task("Приготовить перекус", "Заварить лимонный чай с печаньками", Status.NEW,
                null, null);
        secondTask = new Task("Привести себя в порядок", "Сходить помыться", Status.NEW, null,
                null);
        firstEpic = new Epic("Выйти на прогулку", "Стоять сходить развеяться");
        secondEpic = new Epic("Купить билеты", "Купить билеты для поездки на отдых");
        firstSubtask = new Subtask(firstEpic.hashCode(), "Посмотреть погоду", "Вдруг будет дождь",
                Status.DONE, null, null);
        secondSubtask = new Subtask(firstEpic.hashCode(), "Одеться по погоде", "Нельзя простужаться",
                Status.NEW, null, null);
    }

    @AfterAll
    public static void afterAll() {
        server.stop();
    }

    @Test
    public void serverCanReturnAllExistingTasks() throws IOException, InterruptedException {
        String firstTaskJson = gson.toJson(firstTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstTask, manager.getTask(firstTask.hashCode()), "Задача не создалась");

        String secondTaskJson = gson.toJson(secondTask);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(secondTask, manager.getTask(secondTask.hashCode()), "Задача не создалась");
        firstTask.setId(firstTask.hashCode());
        secondTask.setId(secondTask.hashCode());

        List<Task> taskList = new ArrayList<>();

        taskList.add(secondTask);
        taskList.add(firstTask);

        String taskListJson = gson.toJson(taskList);

        url = URI.create("http://localhost:8080/tasks/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskListJson, response.body(), "Возвращает некорректный список задач");
    }

    @Test
    public void serverCanReturnExistingTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(firstTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstTask, manager.getTask(firstTask.hashCode()), "Задача не создалась");
        firstTask.setId(firstTask.hashCode());
        taskJson = gson.toJson(firstTask);
        url = URI.create("http://localhost:8080/tasks/" + firstTask.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskJson, response.body(), "Не возвращает запрашиваемую задачу");
    }

    @Test
    public void serverCorrectlyHandleSearchNonExistingTask() throws IOException, InterruptedException {
        assertThrows(NotFoundException.class, () -> manager.getTask(firstTask.getId()), "Задача существует в менеджере");
        url = URI.create("http://localhost:8080/tasks/" + firstTask.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Сервер вернул неверный код");
    }

    @Test
    public void serverCanCreateRequestedTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(firstTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstTask, manager.getTask(firstTask.hashCode()), "Задача не создалась");
    }

    @Test
    public void serverCorrectlyHandleCreatingTaskWithNonAcceptableTime() throws IOException, InterruptedException {
        LocalDateTime localTime = LocalDateTime.now();

        firstTask.setStartTime(localTime);
        firstTask.setDuration(Duration.ofMinutes(5));

        String firstTaskJson = gson.toJson(firstTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(firstTask, manager.getTask(firstTask.hashCode()), "Задача не создалась");
        secondTask.setStartTime(localTime);
        secondTask.setDuration(Duration.ofMinutes(5));

        String secondTaskJson = gson.toJson(secondTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Сервер вернул неверный код");
    }

    @Test
    public void serverCanDeleteRequestedTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(firstTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstTask, manager.getTask(firstTask.hashCode()), "Задача не создалась");
        url = URI.create("http://localhost:8080/tasks/" + firstTask.hashCode());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер вернул неверный код");
        assertThrows(NotFoundException.class, () -> manager.getTask(firstTask.hashCode()), "Задача не удалилась");
    }

    @Test
    public void serverCanReturnAllExistingSubtasks() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");

        String firstSubtaskJson = gson.toJson(firstSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstSubtask, manager.getSubtask(firstSubtask.hashCode()), "Подзадача не создалась");

        String secondSubtaskJson = gson.toJson(secondSubtask);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(secondSubtask, manager.getSubtask(secondSubtask.hashCode()), "Подзадача не создалась");
        firstSubtask.setId(firstSubtask.hashCode());
        secondSubtask.setId(secondSubtask.hashCode());

        List<Subtask> subtaskList = new ArrayList<>();

        subtaskList.add(secondSubtask);
        subtaskList.add(firstSubtask);

        String subtaskListJson = gson.toJson(subtaskList);

        url = URI.create("http://localhost:8080/subtasks/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(subtaskListJson, response.body(), "Возвращает некорректный список подзадач");
    }

    @Test
    public void serverCanReturnExistingSubtask() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");

        String subtaskJson = gson.toJson(firstSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstSubtask, manager.getSubtask(firstSubtask.hashCode()), "Подзадача не создалась");
        firstSubtask.setId(firstSubtask.hashCode());
        subtaskJson = gson.toJson(firstSubtask);
        url = URI.create("http://localhost:8080/subtasks/" + firstSubtask.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(subtaskJson, response.body(), "Не возвращает запрашиваемую подзадачу");
    }

    @Test
    public void serverCorrectlyHandleSearchNonExistingSubtask() throws IOException, InterruptedException {
        assertThrows(NotFoundException.class, () -> manager.getSubtask(firstSubtask.getId()), "Подзадача существует в менеджере");
        url = URI.create("http://localhost:8080/subtasks/" + firstSubtask.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Сервер вернул неверный код");
    }

    @Test
    public void serverCanCreateRequestedSubtask() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");

        String subtaskJson = gson.toJson(firstSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstSubtask, manager.getSubtask(firstSubtask.hashCode()), "Задача не создалась");
    }

    @Test
    public void serverCorrectlyHandleCreatingSubtaskWithNonAcceptableTime() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");

        LocalDateTime localTime = LocalDateTime.now();

        firstSubtask.setStartTime(localTime);
        firstSubtask.setDuration(Duration.ofMinutes(5));

        String firstSubtaskJson = gson.toJson(firstSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(firstSubtask, manager.getSubtask(firstSubtask.hashCode()), "Подзадача не создалась");
        secondSubtask.setStartTime(localTime);
        secondSubtask.setDuration(Duration.ofMinutes(5));

        String secondSubtaskJson = gson.toJson(secondSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Сервер вернул неверный код");
    }

    @Test
    public void serverCanDeleteRequestedSubtask() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");

        String subtaskJson = gson.toJson(firstSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstSubtask, manager.getSubtask(firstSubtask.hashCode()), "Подзадача не создалась");
        url = URI.create("http://localhost:8080/subtasks/" + firstSubtask.hashCode());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер вернул неверный код");
        assertThrows(NotFoundException.class, () -> manager.getSubtask(firstSubtask.hashCode()), "Подзадача не удалилась");
    }

    @Test
    public void serverCanReturnAllExistingEpics() throws IOException, InterruptedException {
        String firstEpicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstEpicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");

        String secondEpicJson = gson.toJson(secondEpic);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondEpicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(secondEpic, manager.getEpic(secondEpic.hashCode()), "Эпик не создался");
        firstEpic.setId(firstEpic.hashCode());
        secondEpic.setId(secondEpic.hashCode());

        List<Epic> epicList = new ArrayList<>();

        epicList.add(firstEpic);
        epicList.add(secondEpic);

        String epicListJson = gson.toJson(epicList);

        url = URI.create("http://localhost:8080/epics/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(epicListJson, response.body(), "Возвращает некорректный список эпиков");
    }

    @Test
    public void serverCanReturnExistingEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");
        firstEpic.setId(firstEpic.hashCode());
        epicJson = gson.toJson(firstEpic);
        url = URI.create("http://localhost:8080/epics/" + firstEpic.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(epicJson, response.body(), "Не возвращает запрашиваемый эпик");
    }

    @Test
    public void serverCorrectlyHandleSearchNonExistingEpic() throws IOException, InterruptedException {
        assertThrows(NotFoundException.class, () -> manager.getEpic(firstEpic.getId()), "Эпик существует в менеджере");
        url = URI.create("http://localhost:8080/epics/" + firstEpic.getId());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Сервер вернул неверный код");
    }

    @Test
    public void serverCanReturnExistingSubtasksByEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");
        firstSubtask.setEpicId(firstEpic.hashCode());

        String firstSubtaskJson = gson.toJson(firstSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstSubtask, manager.getSubtask(firstSubtask.hashCode()), "Подзадача не создалась");
        secondSubtask.setEpicId(firstEpic.hashCode());

        String secondSubtaskJson = gson.toJson(secondSubtask);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(secondSubtask, manager.getSubtask(secondSubtask.hashCode()), "Подзадача не создалась");
        firstSubtask.setId(firstSubtask.hashCode());
        secondSubtask.setId(secondSubtask.hashCode());
        firstEpic.setId(firstEpic.hashCode());

        List<Subtask> subtasksByEpic = new ArrayList<>();
        subtasksByEpic.add(firstSubtask);
        subtasksByEpic.add(secondSubtask);

        String subtaskListJson = gson.toJson(subtasksByEpic);

        url = URI.create("http://localhost:8080/epics/" + firstEpic.hashCode() + "/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(subtaskListJson, response.body(), "Возвращает некорректный список подзадач по эпику");
    }

    @Test
    public void serverCorrectlyHandleSearchNonExistingSubtasksByEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");
        firstSubtask.setEpicId(firstEpic.hashCode());

        String firstSubtaskJson = gson.toJson(firstSubtask);

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstSubtask, manager.getSubtask(firstSubtask.hashCode()), "Подзадача не создалась");
        secondSubtask.setEpicId(firstEpic.hashCode());

        String secondSubtaskJson = gson.toJson(secondSubtask);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondSubtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(secondSubtask, manager.getSubtask(secondSubtask.hashCode()), "Подзадача не создалась");
        firstSubtask.setId(firstSubtask.hashCode());
        secondSubtask.setId(secondSubtask.hashCode());

        url = URI.create("http://localhost:8080/epics/" + secondEpic.hashCode() + "/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Сервер вернул неверный код");
    }

    @Test
    public void serverCanCreateRequestedEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");
    }

    @Test
    public void serverCanDeleteRequestedEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(firstEpic);

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstEpic, manager.getEpic(firstEpic.hashCode()), "Эпик не создался");
        url = URI.create("http://localhost:8080/epics/" + firstEpic.hashCode());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер вернул неверный код");
        assertThrows(NotFoundException.class, () -> manager.getEpic(firstEpic.hashCode()), "Эпик не удалился");
    }

    @Test
    public void serverCanReturnHistoryTaskOpening() throws IOException, InterruptedException {
        String firstTaskJson = gson.toJson(firstTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        firstTask.setId(firstTask.hashCode());
        firstTaskJson = gson.toJson(firstTask);

        List<Task> taskHistoryList = new ArrayList<>();
        String taskHistoryListJson = gson.toJson(taskHistoryList);

        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskHistoryListJson, response.body(), "Возвращает некорректную историю просмотра задач");
        url = URI.create("http://localhost:8080/tasks/" + firstTask.hashCode());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Сервер вернул неверный код");
        assertEquals(firstTaskJson, response.body(), "Не возвращает запрашиваемую задачу");
        taskHistoryList.add(firstTask);
        taskHistoryListJson = gson.toJson(taskHistoryList);
        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(taskHistoryListJson, response.body(), "Возвращает некорректную историю просмотра задач");
    }

    @Test
    public void serverCanReturnPrioritizedTaskList() throws IOException, InterruptedException {
        LocalDateTime localTime = LocalDateTime.now();

        firstTask.setStartTime(localTime);
        firstTask.setDuration(Duration.ofMinutes(5));

        String firstTaskJson = gson.toJson(firstTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(firstTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        firstTask.setId(firstTask.hashCode());
        secondTask.setStartTime(localTime.minusMinutes(10));
        secondTask.setDuration(Duration.ofMinutes(5));

        String secondTaskJson = gson.toJson(secondTask);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(secondTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Сервер вернул неверный код");
        secondTask.setId(secondTask.hashCode());

        List<Task> prioritizedTaskList = new ArrayList<>();

        prioritizedTaskList.add(secondTask);
        prioritizedTaskList.add(firstTask);

        String prioritizedTaskListJson = gson.toJson(prioritizedTaskList);

        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(prioritizedTaskListJson, response.body(), "Возвращает некорректный список задач по приоритету");
    }
}