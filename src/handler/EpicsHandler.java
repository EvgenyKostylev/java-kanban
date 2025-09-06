package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import com.google.gson.Gson;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");

        if (pathParts.length > 2) {
            Optional<Integer> epicIdOptional = getTaskId(pathParts[2]);

            if (epicIdOptional.isPresent()) {
                Epic epic = manager.getEpic(epicIdOptional.get());

                if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                    sendText(httpExchange, gson.toJson(manager.getSubtasksByEpic(epic)));
                } else {
                    sendText(httpExchange, gson.toJson(epic));
                }
            } else {
                sendBadRequest(httpExchange);
            }
        } else {
            sendText(httpExchange, gson.toJson(manager.getEpics()));
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            String jsonEpic = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(jsonEpic, Epic.class);

            manager.createEpic(epic);
            sendNull(httpExchange);
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        Optional<Integer> epicIdOptional = getTaskId(pathParts[2]);

        if (epicIdOptional.isPresent()) {
            sendText(httpExchange, gson.toJson(manager.removeEpic(epicIdOptional.get())));
        } else {
            sendBadRequest(httpExchange);
        }
    }
}