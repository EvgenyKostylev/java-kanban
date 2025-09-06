package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.NotAcceptableTimeException;
import exception.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGetRequest(httpExchange);
                case "POST":
                    handlePostRequest(httpExchange);
                case "DELETE":
                    handleDeleteRequest(httpExchange);
                default:
                    sendNotFound(httpExchange);
            }
        } catch (NotFoundException exception) {
            sendNotFound(httpExchange);
        } catch (ManagerSaveException exception) {
            sendInternalServerError(httpExchange);
        } catch (NotAcceptableTimeException exception) {
            sendHasInteractions(httpExchange);
        }
    }

    protected abstract void handleGetRequest(HttpExchange httpExchange) throws IOException;

    protected abstract void handlePostRequest(HttpExchange httpExchange) throws IOException;

    protected abstract void handleDeleteRequest(HttpExchange httpExchange) throws IOException;

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendNull(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(201, 0);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(406, 0);
        httpExchange.close();
    }

    protected void sendInternalServerError(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(500, 0);
        httpExchange.close();
    }

    protected void sendBadRequest(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(400, 0);
        httpExchange.close();
    }

    protected Optional<Integer> getTaskId(String pathPartId) {
        try {
            return Optional.of(Integer.parseInt(pathPartId));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}