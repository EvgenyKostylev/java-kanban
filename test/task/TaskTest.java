package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    private static Task firstTask;
    private static Task secondTask;

    @BeforeAll
    public static void beforeAll() {
        Task task = new Task("Сделать подарок Матвею", "Отправить посылку Матвею по почте",
                Status.NEW, null, null);

        firstTask = task;
        secondTask = task;
    }

    @Test
    public void taskWithSameIdAreEqual() {
        assertEquals(firstTask.getId(), secondTask.getId(), "Id задач не равны");
        assertEquals(firstTask, secondTask, "Задачи не равны");
    }
}