package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {
    private static Subtask firstSubtask;
    private static Subtask secondSubtask;

    @BeforeAll
    public static void beforeAll() {
        Subtask subtask = new Subtask(1, "Достать продукты", "Достать из холодильника продукты",
                Status.NEW, null, null);

        firstSubtask = subtask;
        secondSubtask = subtask;
    }

    @Test
    public void subtaskWithSameIdAreEqual() {
        if (firstSubtask.getId() == secondSubtask.getId()) {
            assertEquals(firstSubtask, secondSubtask, "Подзадачи не равны");
        }
    }

    @Test
    public void subtaskCannotBeEpicForItself() {
        firstSubtask.setEpicId(firstSubtask.getId());
        assertNotEquals(firstSubtask.getId(), firstSubtask.getEpicId(),
                "Подзадача не может быть самой для себя Эпиком");
    }
}