package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import com.google.gson.Gson;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");

        if (pathParts.length > 2) {
            Optional<Integer> taskIdOptional = getTaskId(pathParts[2]);

            if (taskIdOptional.isPresent()) {
                sendText(httpExchange, gson.toJson(manager.getTask(taskIdOptional.get())));
            } else {
                sendBadRequest(httpExchange);
            }
        } else {
            sendText(httpExchange, gson.toJson(manager.getTasks()));
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(jsonTask, Task.class);

            if (task.getId() == 0) {
                manager.createTask(task);
            } else {
                manager.updateTask(task);
            }
            sendNull(httpExchange);
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOptional = getTaskId(pathParts[2]);

        if (taskIdOptional.isPresent()) {
            sendText(httpExchange, gson.toJson(manager.removeTask(taskIdOptional.get())));
        } else {
            sendBadRequest(httpExchange);
        }
    }
}