package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import com.google.gson.Gson;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");

        if (pathParts.length > 2) {
            Optional<Integer> subtaskIdOptional = getTaskId(pathParts[2]);

            if (subtaskIdOptional.isPresent()) {
                sendText(httpExchange, gson.toJson(manager.getSubtask(subtaskIdOptional.get())));
            } else {
                sendBadRequest(httpExchange);
            }
        } else {
            sendText(httpExchange, gson.toJson(manager.getSubtasks()));
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            String jsonSubtask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);

            if (subtask.getId() == 0) {
                manager.createSubtask(subtask);
            } else {
                manager.updateSubtask(subtask);
            }
            sendNull(httpExchange);
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        Optional<Integer> subtaskIdOptional = getTaskId(pathParts[2]);

        if (subtaskIdOptional.isPresent()) {
            sendText(httpExchange, gson.toJson(manager.removeSubtask(subtaskIdOptional.get())));
        } else {
            sendBadRequest(httpExchange);
        }
    }
}