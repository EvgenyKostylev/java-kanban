package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static TaskManager manager;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;
    private static File file;
    private static File emptyFile;
    private static FileBackedTaskManager emptyManager;
    private static final int ZERO_LENGTH_FILE = 0;

    @BeforeAll
    public static void beforeAll() {
        try {
            file = File.createTempFile("ManagerTestTmp", ".txt");
        } catch (IOException e) {
            System.out.println("Не удалось создать временный файл:");
            e.printStackTrace();
        }
        manager = FileBackedTaskManager.loadFromFile(file);
        task = new Task("Сделать подарок Матвею", "Отправить посылку Матвею по почте", Status.NEW);
        epic = new Epic("Приготовить обед", "Приготовить яишницу по канадски");
        subtask = new Subtask(epic.getId(), "Достать продукты", "Достать из холодильника продукты",
                Status.NEW);
        try {
            emptyFile = File.createTempFile("ManagerTestEmptyTmp", ".txt");
        } catch (IOException e) {
            System.out.println("Не удалось создать временный файл:");
            e.printStackTrace();
        }
    }

    @AfterEach
    public void afterEach() {
        manager.removeTasks();
        manager.removeEpics();
        manager.removeSubtasks();
        emptyManager = null;
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

    @Test
    public void managerAcceptEmptyFile() {
        assertEquals(ZERO_LENGTH_FILE, emptyFile.length(), "Файл содержит данные");
        assertDoesNotThrow(() -> emptyManager = FileBackedTaskManager.loadFromFile(emptyFile),
                "Менеджер не принял пустой файл");
    }

    @Test
    public void managerAcceptNotEmptyFile() {
        manager.createTask(task);
        assertNotEquals(ZERO_LENGTH_FILE, file.length(), "Файл не содержит данные");
        assertDoesNotThrow(() -> emptyManager = FileBackedTaskManager.loadFromFile(file),
                "Менеджер не принял файл с данными");
    }

    @Test
    public void managerLoadMultipleTasks() {
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        emptyManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(task, emptyManager.getTask(task.getId()), "Менеджер не загрузил задачу");
        assertEquals(epic, emptyManager.getEpic(epic.getId()), "Менеджер не загрузил эпик");
        assertEquals(subtask, emptyManager.getSubtask(subtask.getId()), "Менеджер не загрузит подзадачу");
    }

    @Test
    public void managerSaveMultipleTasks() {
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        assertNotEquals(emptyFile.length(), file.length(), "Менеджер не сохранил задачи");
    }
}