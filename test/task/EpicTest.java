package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EpicTest {
    private static Epic firstEpic;
    private static Epic secondEpic;
    private static ArrayList<Subtask> subtaskList;
    private static Subtask firstSubtask;
    private static Subtask secondSubtask;

    @BeforeAll
    public static void beforeAll() {
        Epic epic = new Epic("Приготовить обед", "Приготовить яишницу по канадски");

        firstEpic = epic;
        secondEpic = epic;
    }

    @BeforeEach
    public void beforeEach() {
        firstSubtask = new Subtask(firstEpic.getId(), "Достать продукты",
                "Достать из холодильника яйца и кетчуп", Status.NEW, null, null);
        secondSubtask = new Subtask(firstEpic.getId(), "Достать продукты",
                "Достать из холодильника яйца и кетчуп", Status.NEW, null, null);
        subtaskList = new ArrayList<>();
    }

    @Test
    public void epicWithSameIdAreEqual() {
        if (firstEpic.getId() == secondEpic.getId()) {
            assertEquals(firstEpic, secondEpic, "Эпики не равны");
        }
    }

    @Test
    public void addEpicToItselfAsSubtask() {
        firstSubtask.setId(firstEpic.getId());
        subtaskList.add(firstSubtask);
        firstEpic.setSubtasks(subtaskList);
        firstEpic.getSubtasks().forEach(subtask -> assertNotEquals(firstEpic.getId(), subtask.getId(),
                "Эпик не может содержась самого себя в качестве подзадачи"));
    }

    @Test
    public void epicHasStatusNewIfAllSubtasksHasStatusNew() {
        assertEquals(Status.NEW, firstSubtask.getStatus(), "Статус подзадачи не NEW");
        subtaskList.add(firstSubtask);
        assertEquals(Status.NEW, secondSubtask.getStatus(), "Статус подзадачи не NEW");
        subtaskList.add(secondSubtask);
        firstEpic.setSubtasks(subtaskList);
        assertEquals(Status.NEW, firstEpic.getStatus(),
                "Статус эпика не соответствует действительности");
    }

    @Test
    public void epicHasStatusDoneIfAllSubtasksHasStatusDone() {
        firstSubtask.setStatus(Status.DONE);
        assertEquals(Status.DONE, firstSubtask.getStatus(), "Статус подзадачи не DONE");
        subtaskList.add(firstSubtask);
        secondSubtask.setStatus(Status.DONE);
        assertEquals(Status.DONE, secondSubtask.getStatus(), "Статус подзадачи не DONE");
        subtaskList.add(secondSubtask);
        firstEpic.setSubtasks(subtaskList);
        assertEquals(Status.DONE, firstEpic.getStatus(),
                "Статус эпика не соответствует действительности");
    }

    @Test
    public void epicHasStatusInProgressIfAllSubtasksHasStatusNewAndDone() {
        assertEquals(Status.NEW, firstSubtask.getStatus(), "Статус подзадачи не NEW");
        subtaskList.add(firstSubtask);
        secondSubtask.setStatus(Status.DONE);
        assertEquals(Status.DONE, secondSubtask.getStatus(), "Статус подзадачи не DONE");
        subtaskList.add(secondSubtask);
        firstEpic.setSubtasks(subtaskList);
        assertEquals(Status.IN_PROGRESS, firstEpic.getStatus(),
                "Статус эпика не соответствует действительности");
    }

    @Test
    public void epicHasStatusInProgressIfAllSubtasksHasStatusInProgress() {
        assertEquals(Status.NEW, firstSubtask.getStatus(), "Статус подзадачи не NEW");
        subtaskList.add(firstSubtask);
        secondSubtask.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, secondSubtask.getStatus(), "Статус подзадачи не IN_PROGRESS");
        subtaskList.add(secondSubtask);
        firstEpic.setSubtasks(subtaskList);
        assertEquals(Status.IN_PROGRESS, firstEpic.getStatus(),
                "Статус эпика не соответствует действительности");
    }
}