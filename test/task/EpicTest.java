package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EpicTest {
    private static Epic firstEpic;
    private static Epic secondEpic;
    private static ArrayList<Subtask> subtaskList;

    @BeforeAll
    public static void beforeAll() {
        subtaskList = new ArrayList<>();
        Epic epic = new Epic("Приготовить обед", "Приготовить яишницу по канадски");

        firstEpic = epic;
        secondEpic = epic;
    }

    @Test
    public void epicWithSameIdAreEqual() {
        if (firstEpic.getId() == secondEpic.getId()) {
            assertEquals(firstEpic, secondEpic, "Эпики не равны");
        }
    }

    @Test
    public void addEpicToItselfAsSubtask() {
        Subtask subtask = new Subtask(firstEpic.getId(), "Достать продукты",
                "Достать из холодильника яйца и кетчуп", Status.NEW);

        subtask.setId(firstEpic.getId());
        subtaskList.add(subtask);
        firstEpic.setSubtasks(subtaskList);

        for (Subtask subtaskByEpic : firstEpic.getSubtasks()) {
            assertNotEquals(subtaskByEpic.getId(), firstEpic.getId(),
                    "Эпик не может содержась самого себя в качестве подзадачи");
        }
    }
}