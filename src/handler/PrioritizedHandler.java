package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import com.google.gson.Gson;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    protected void handleGetRequest(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, gson.toJson(manager.getPrioritizedTasks()));
    }

    @Override
    protected void handlePostRequest(HttpExchange httpExchange) throws IOException {
        sendBadRequest(httpExchange);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange httpExchange) throws IOException {
        sendBadRequest(httpExchange);
    }
}