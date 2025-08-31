package manager;

import exception.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    protected static File file;
    protected static File emptyFile;
    protected static FileBackedTaskManager emptyManager;
    protected static final int ZERO_LENGTH_FILE = 0;

    @BeforeAll
    public static void beforeAll() {
        try {
            file = File.createTempFile("ManagerTestTmp", ".txt");
            emptyFile = File.createTempFile("ManagerTestEmptyTmp", ".txt");
        } catch (IOException e) {
            System.out.println("Не удалось создать временный файл:");
            e.printStackTrace();
        }
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Override
    @AfterEach
    public void afterEach() {
        super.afterEach();
        emptyManager = null;
    }

    @Test
    public void managerAcceptEmptyFile() {
        assertEquals(ZERO_LENGTH_FILE, emptyFile.length(), "Файл содержит данные");
        assertDoesNotThrow(() -> emptyManager = FileBackedTaskManager.loadFromFile(emptyFile),
                "Менеджер не принял пустой файл");
    }

    @Test
    public void managerAcceptNotEmptyFile() {
        taskManager.createTask(task);
        assertNotEquals(ZERO_LENGTH_FILE, file.length(), "Файл не содержит данные");
        assertDoesNotThrow(() -> emptyManager = FileBackedTaskManager.loadFromFile(file),
                "Менеджер не принял файл с данными");
    }

    @Test
    public void managerAcceptFileHaveEpicWithoutSubtasks() {
        taskManager.createEpic(epic);
        assertDoesNotThrow(() -> emptyManager = FileBackedTaskManager.loadFromFile(file),
                "Менеджер не принял файл с эпиком без подзадач");
    }

    @Test
    public void managerLoadMultipleTasks() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);
        emptyManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(task, emptyManager.getTask(task.getId()), "Менеджер не загрузил задачу");
        assertEquals(epic, emptyManager.getEpic(epic.getId()), "Менеджер не загрузил эпик");
        assertEquals(firstSubtask, emptyManager.getSubtask(firstSubtask.getId()),
                "Менеджер не загрузит подзадачу");
    }

    @Test
    public void managerSaveMultipleTasks() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);
        assertNotEquals(emptyFile.length(), file.length(), "Менеджер не сохранил задачи");
    }

    @Test
    public void specifyingNonExistFileCausesManagerSaveException() {
        File NonExistFile = new File("C:/NonExistentDirectory/NonExistentFile.txt");

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(NonExistFile),
                "Менеджер перехватывает некорректное исключение");
    }
}