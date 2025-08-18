package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest<T extends HistoryManager> {
    protected T historyManager;
    protected static Task task;
    protected static Task secondTask;
    protected static Task thirdTask;

    protected abstract T createHistoryManager();

    @BeforeEach
    public void beforeEach() {
        historyManager = createHistoryManager();
    }

    @BeforeAll
    public static void beforeAll() {
        task = new Task("Сделать подарок Матвею", "Отправить посылку Матвею по почте",
                Status.NEW, null, null);
        secondTask = new Task("Сходить погулять", "Выйти на улицу", Status.NEW, null,
                null);
        thirdTask = new Task("Почистить память компьютера", "Удалить ненужные файлы с компьютера",
                Status.NEW, null, null);
    }

    @Test
    public void historySavePreviousTaskVersion() {
        historyManager.add(task);

        Task previousVersionTask = new Task(task.getName(), task.getDescription(), task.getStatus(),
                task.getStartTime(), task.getDuration());

        task.setName("Прибраться");

        List<Task> history = historyManager.getHistory();
        Task historySavedTask = history.getLast();

        assertNotEquals(previousVersionTask, historySavedTask, "История сохранила актуальную версию задачи");
    }

    @Test
    public void historyStoreTasksWithoutRepetition() {
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История сохраняет повторный просмотр задач");
    }

    @Test
    public void historySaveCorrectVersionOfTask() {
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(task, history.getFirst(), "История неправильно сохранила задачу");
    }

    @Test
    public void historyCorrectlyDeleteFirstTask() {
        historyManager.add(task);
        historyManager.add(secondTask);
        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(secondTask, history.getFirst(), "История удалила требуемую задачу");
        assertEquals(1, history.size(), "История сохраняет неверное количество задач после удаления");
    }

    @Test
    public void historyCorrectlyDeleteLastTask() {
        historyManager.add(task);
        historyManager.add(secondTask);
        historyManager.remove(secondTask.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(task, history.getFirst(), "История удалила требуемую задачу");
        assertEquals(1, history.size(), "История сохраняет неверное количество задач после удаления");
    }

    @Test
    public void historyCorrectlyDeleteMiddleTask() {
        historyManager.add(task);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);
        historyManager.remove(secondTask.getId());

        List<Task> history = historyManager.getHistory();
        List<Task> expectedHistory = new ArrayList<>();

        expectedHistory.add(task);
        expectedHistory.add(thirdTask);
        assertEquals(expectedHistory, history, "История удалила требуемую задачу");
        assertEquals(2, history.size(), "История сохраняет неверное количество задач после удаления");
    }

    @Test
    public void historyCanExistWithoutTask() {
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertEquals(new ArrayList<>(), historyManager.getHistory(),
                "История не может существовать без задач");
    }
}