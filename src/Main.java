import task.*;
import util.*;

public class Main {
    private static final TaskManager manager = Managers.getDefault();

    public static void main(String[] args) {
        Task task1 = new Task("Приготовить перекус", "Заварить лимонный чай с печаньками", Status.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Привести себя в порядок", "Сходить помыться", Status.NEW);
        manager.createTask(task2);
        Epic epic1 = new Epic("Выйти на прогулку", "Стоять сходить развеяться");
        manager.createEpic(epic1);
        Subtask subtask1epic1 = new Subtask(epic1.getId(), "Одеться по погоде", "Нельзя простужаться", Status.NEW);
        manager.createSubtask(subtask1epic1);
        Subtask subtask2epic1 = new Subtask(epic1.getId(), "Взять наушники", "Без музики гулять скучно", Status.NEW);
        manager.createSubtask(subtask2epic1);
        Epic epic2 = new Epic("Выйти в лес", "Лето началась, пора проводить время на природе");
        manager.createEpic(epic2);
        Subtask subtask1epic2 = new Subtask(epic2.getId(), "Побрызгать", "Клещи бунтуют в лесу!", Status.NEW);
        manager.createSubtask(subtask1epic2);
        printAllTasks();
        task1.setStatus(Status.DONE);
        manager.updateTask(task1.getId(), task1);
        task2.setStatus(Status.DONE);
        manager.updateTask(task2.getId(), task2);
        epic1.setStatus(Status.NEW);
        manager.updateEpic(epic1.getId(), epic1);
        subtask1epic1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1epic1.getId(), subtask1epic1);
        epic2.setStatus(Status.IN_PROGRESS);
        manager.updateEpic(epic2.getId(), epic2);
        subtask1epic2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1epic2.getId(), subtask1epic2);
        printAllTasks();
        manager.removeTask(task1.getId());
        manager.removeEpic(epic1.getId());
        printAllTasks();
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : epic.getSubtasks()) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
