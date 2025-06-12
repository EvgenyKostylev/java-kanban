import task.*;
import util.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Приготовить перекус", "Заварить лимонный чай с печаньками", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Привести себя в порядок", "Сходить помыться", Status.NEW);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Выйти на прогулку", "Стоять сходить развеяться");
        taskManager.createEpic(epic1);
        Subtask subtask1epic1 = new Subtask(epic1.getId(), "Одеться по погоде", "Нельзя простужаться", Status.NEW);
        taskManager.createSubtask(subtask1epic1);
        Subtask subtask2epic1 = new Subtask(epic1.getId(), "Взять наушники", "Без музики гулять скучно", Status.NEW);
        taskManager.createSubtask(subtask2epic1);
        Epic epic2 = new Epic("Выйти в лес", "Лето началась, пора проводить время на природе");
        taskManager.createEpic(epic2);
        Subtask subtask1epic2 = new Subtask(epic2.getId(), "Побрызгать", "Клещи бунтуют в лесу!", Status.NEW);
        taskManager.createSubtask(subtask1epic2);
        System.out.println("Добавили задачи");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1.getId(), task1);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2.getId(), task2);
        epic1.setStatus(Status.NEW);
        taskManager.updateEpic(epic1.getId(), epic1);
        subtask1epic1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1epic1.getId(), subtask1epic1);
        epic2.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic2.getId(), epic2);
        subtask1epic2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1epic2.getId(), subtask1epic2);
        System.out.println("Отредактировали задачи");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic1.getId());
        System.out.println("Удалили пару задач");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }
}
