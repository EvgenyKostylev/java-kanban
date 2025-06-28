package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    private static HistoryManager historyManager;
    private static Task task;

    @BeforeAll
    public static void beforeAll() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Сделать подарок Матвею", "Отправить посылку Матвею по почте", Status.NEW);
    }

    @Test
    public void historySavePreviousTaskVersion() {
        historyManager.add(task);

        Task previousVersionTask = new Task(task.getName(), task.getDescription(), task.getStatus());

        task.setName("Прибраться");

        List<Task> history = historyManager.getHistory();
        Task historySavedTask = history.getLast();

        assertEquals(previousVersionTask, historySavedTask, "История не сохранила предыдушую версию задачи");
    }

}