package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {
    private static TaskManager manager;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;

    @BeforeAll
    public static void beforeAll() {
        manager = new InMemoryTaskManager();
        task = new Task("Сделать подарок Матвею", "Отправить посылку Матвею по почте", Status.NEW);
        epic = new Epic("Приготовить обед", "Приготовить яишницу по канадски");
        subtask = new Subtask(epic.getId(), "Достать продукты", "Достать из холодильника продукты",
                Status.NEW);
    }

    @AfterEach
    public void afterEach() {
        manager.removeTasks();
        manager.removeEpics();
        manager.removeSubtasks();
    }

    @Test
    public void managerCanFindCreatedTaskById() {
        manager.createTask(task);

        ArrayList<Task> taskList = manager.getTasks();

        assertEquals(task, taskList.getFirst(), "Менеджер не добавляет задачи");
        assertEquals(task, manager.getTask(task.getId()), "Менеджер не находит задачи по Id");
    }

    @Test
    public void managerCanFindCreatedEpicById() {
        manager.createEpic(epic);

        ArrayList<Epic> epicList = manager.getEpics();

        assertEquals(epic, epicList.getFirst(), "Менеджер не добавляет эпики");
        assertEquals(epic, manager.getEpic(epic.getId()), "Менеджер не находит эпики по Id");
    }

    @Test
    public void managerCanFindCreatedSubtaskById() {
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        ArrayList<Subtask> subtaskList = manager.getSubtasks();

        assertEquals(subtask, subtaskList.getFirst(), "Менеджер не добавляет подзадачи");
        assertEquals(subtask, manager.getSubtask(subtask.getId()), "Менеджер не находит подзадачи по Id");
    }

    @Test
    public void tasksWithSpecifiedAndGeneratedIdDoNotConflict() {
        Task taskWithSpecifiedId = new Task("Постирать вещи",
                "Закинуть грязные вещи в стиркальную машину", Status.DONE);

        taskWithSpecifiedId.setId(123164611);
        manager.createTask(taskWithSpecifiedId);
        manager.createTask(task);
        assertEquals(2, manager.getTasks().size(),
                "У задач с разной вариацией определения Id возник конфликт");
    }

    @Test
    public void createdTaskDoesNotChangeWhenAdd() {
        Task newTask = new Task("Постирать вещи",
                "Закинуть грязные вещи в стиркальную машину", Status.DONE);

        newTask.setId(123164611);
        manager.createTask(newTask);

        ArrayList<Task> taskList = manager.getTasks();
        Task addedTask = taskList.getFirst();

        assertEquals(newTask.getName(), addedTask.getName(), "После добавления изменилось имя задачи");
        assertEquals(newTask.getDescription(), addedTask.getDescription(),
                "После добавления изменилось описание задачи");
        assertEquals(newTask.getId(), addedTask.getId(), "После добавления изменился Id задачи");
        assertEquals(newTask.getStatus(), addedTask.getStatus(), "После добавления изменился статус задачи");
    }

    @Test
    public void epicKeepActualSubtaskId() {
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask(subtask.getEpicId(), subtask.getName(), subtask.getDescription(),
                subtask.getStatus());

        updatedSubtask.setId(subtask.getId());
        manager.updateSubtask(updatedSubtask);

        List<Subtask> subtaskByEpic = manager.getSubtasksByEpic(epic);

        assertEquals(updatedSubtask, subtaskByEpic.getFirst(), "Сохранился неуктуальный Id подзадачи");
        assertEquals(1, subtaskByEpic.size(),
                "Сохранился неактуальный Id подзадачи вместе с актуальным");
        manager.removeSubtasks();

        subtaskByEpic = manager.getSubtasksByEpic(epic);

        assertEquals(0, subtaskByEpic.size(), "Сохранился неактуальный Id подзадачи после удаления");
    }
}