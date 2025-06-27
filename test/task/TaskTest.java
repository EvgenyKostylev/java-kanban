package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    private static Task firstTask;
    private static Task secondTask;

    @BeforeAll
    public static void beforeAll() {
        Task task = new Task("Сделать подарок Матвею", "Отправить посылку Матвею по почте", Status.NEW);

        firstTask = task;
        secondTask = task;
    }

    @Test
    public void taskWithSameIdAreEqual() {
        if (firstTask.getId() == secondTask.getId()) {
            assertEquals(firstTask, secondTask, "Задачи не равны");
        }
    }
}