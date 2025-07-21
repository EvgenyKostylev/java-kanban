import task.*;
import manager.*;

public class Main {
    private static final TaskManager manager = Managers.getDefault();

    public static void main(String[] args) {
        Task task1 = new Task("Приготовить перекус", "Заварить лимонный чай с печаньками", Status.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Привести себя в порядок", "Сходить помыться", Status.NEW);
        manager.createTask(task2);
        Epic epic1 = new Epic("Выйти на прогулку", "Стоять сходить развеяться");
        manager.createEpic(epic1);
        Subtask subtask1epic1 = new Subtask(epic1.getId(), "Одеться по погоде", "Нельзя простужаться",
                Status.NEW);
        manager.createSubtask(subtask1epic1);
        Subtask subtask2epic1 = new Subtask(epic1.getId(), "Взять наушники",
                "Без музики гулять скучно", Status.NEW);
        manager.createSubtask(subtask2epic1);
        Subtask subtask3epic1 = new Subtask(epic1.getId(), "Побрызгать", "Клещи бунтуют в лесу!",
                Status.NEW);
        manager.createSubtask(subtask3epic1);
        Epic epic2 = new Epic("Покушать", "Накрыть стол для ужина");
        manager.createEpic(epic2);


        manager.getEpic(epic1.getId());
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getSubtask(subtask3epic1.getId());
        manager.getSubtask(subtask3epic1.getId());
        manager.getSubtask(subtask1epic1.getId());
        manager.getEpic(epic2.getId());
        manager.getSubtask(subtask2epic1.getId());
        System.out.println(manager.getHistory());

        manager.getTask(task1.getId());
        manager.getEpic(epic2.getId());
        manager.getTask(task2.getId());
        manager.getSubtask(subtask1epic1.getId());
        manager.getSubtask(subtask2epic1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask3epic1.getId());
        manager.getTask(task2.getId());
        System.out.println(manager.getHistory());

        manager.removeTask(task2.getId());
        System.out.println(manager.getHistory());

        manager.removeEpic(epic1.getId());
        System.out.println(manager.getHistory());
    }
}
