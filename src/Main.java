import task.*;
import manager.*;

import java.io.File;
import java.io.IOException;

public class Main {
    private static TaskManager firstManager;
    private static TaskManager secondManager;
    private static File file;

    public static void main(String[] args) {
        try {
            file = File.createTempFile("tmpManagerTest", ".csv");
            firstManager = FileBackedTaskManager.loadFromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Task task1 = new Task("Приготовить перекус", "Заварить лимонный чай с печаньками", Status.NEW,
                null, null);
        firstManager.createTask(task1);
        Task task2 = new Task("Привести себя в порядок", "Сходить помыться", Status.NEW, null,
                null);
        firstManager.createTask(task2);
        Epic epic1 = new Epic("Выйти на прогулку", "Стоять сходить развеяться");
        firstManager.createEpic(epic1);
        Epic epic2 = new Epic("Купить билеты", "Купить билеты для поездки на отдых");
        firstManager.createEpic(epic2);
        Subtask subtask1epic1 = new Subtask(epic1.getId(), "Посмотреть погоду", "Вдруг будет дождь",
                Status.DONE, null, null);
        firstManager.createSubtask(subtask1epic1);
        Subtask subtask2epic1 = new Subtask(epic1.getId(), "Одеться по погоде", "Нельзя простужаться",
                Status.NEW, null, null);
        firstManager.createSubtask(subtask2epic1);
        secondManager = FileBackedTaskManager.loadFromFile(file);
        secondManager.getTask(task1.getId());
        secondManager.getTask(task2.getId());
        secondManager.getEpic(epic1.getId());
        secondManager.getEpic(epic2.getId());
        secondManager.getSubtask(subtask1epic1.getId());
        secondManager.getSubtask(subtask2epic1.getId());
    }
}