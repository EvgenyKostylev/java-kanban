package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected static Task task;
    protected static Epic epic;
    protected static Subtask firstSubtask;
    protected static Subtask secondSubtask;

    protected abstract T createTaskManager();

    @BeforeAll
    public static void commonBeforeAll() {
        task = new Task("Сделать подарок Матвею", "Отправить посылку Матвею по почте", Status.NEW,
                null, null);
        epic = new Epic("Приготовить обед", "Приготовить яишницу по канадски");
        firstSubtask = new Subtask(epic.getId(), "Достать продукты",
                "Достать из холодильника продукты", Status.NEW, null, null);
        secondSubtask = new Subtask(epic.getId(), "Пожарить яишницу", "Пожарить яица на сковородке",
                Status.NEW, null, null);
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    @AfterEach
    public void afterEach() {
        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();
    }

    @Test
    public void managerCanFindCreatedTaskById() {
        taskManager.createTask(task);

        ArrayList<Task> taskList = taskManager.getTasks();

        assertEquals(task, taskList.getFirst(), "Менеджер не добавляет задачи");
        assertEquals(task, taskManager.getTask(task.getId()), "Менеджер не находит задачи по Id");
    }

    @Test
    public void managerCorrectlyHandleSearchNonExistentTask() {
        assertNull(taskManager.getTask(213132),
                "Менеджер некорректно обрабатывает поиск несуществующей задачи");
    }

    @Test
    public void managerCanFindCreatedEpicById() {
        taskManager.createEpic(epic);

        ArrayList<Epic> epicList = taskManager.getEpics();

        assertEquals(epic, epicList.getFirst(), "Менеджер не добавляет эпики");
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Менеджер не находит эпики по Id");
    }

    @Test
    public void managerCorrectlyHandleSearchNonExistentEpic() {
        assertNull(taskManager.getEpic(213132),
                "Менеджер некорректно обрабатывает поиск несуществующего эпика");
    }

    @Test
    public void managerCanFindCreatedSubtaskById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);

        ArrayList<Subtask> subtaskList = taskManager.getSubtasks();

        assertEquals(firstSubtask, subtaskList.getFirst(), "Менеджер не добавляет подзадачи");
        assertEquals(firstSubtask, taskManager.getSubtask(firstSubtask.getId()),
                "Менеджер не находит подзадачи по Id");
    }

    @Test
    public void managerCorrectlyHandleSearchNonExistentSubtask() {
        assertNull(taskManager.getSubtask(213132),
                "Менеджер некорректно обрабатывает поиск несуществующей подзадачи");
    }

    @Test
    public void tasksWithSpecifiedAndGeneratedIdDoNotConflict() {
        Task taskWithSpecifiedId = new Task("Постирать вещи",
                "Закинуть грязные вещи в стиркальную машину", Status.DONE, null, null);

        taskWithSpecifiedId.setId(123164611);
        taskManager.createTask(taskWithSpecifiedId);
        taskManager.createTask(task);
        assertEquals(2, taskManager.getTasks().size(),
                "У задач с разной вариацией определения Id возник конфликт");
    }

    @Test
    public void createdTaskDoesNotChangeWhenAdd() {
        Task newTask = new Task("Постирать вещи",
                "Закинуть грязные вещи в стиркальную машину", Status.DONE, null, null);

        newTask.setId(123164611);
        taskManager.createTask(newTask);

        ArrayList<Task> taskList = taskManager.getTasks();
        Task addedTask = taskList.getFirst();

        assertEquals(newTask.getName(), addedTask.getName(), "После добавления изменилось имя задачи");
        assertEquals(newTask.getDescription(), addedTask.getDescription(),
                "После добавления изменилось описание задачи");
        assertEquals(newTask.getId(), addedTask.getId(), "После добавления изменился Id задачи");
        assertEquals(newTask.getStatus(), addedTask.getStatus(), "После добавления изменился статус задачи");
    }

    @Test
    public void epicKeepActualSubtaskId() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);

        Subtask updatedSubtask = new Subtask(firstSubtask.getEpicId(), firstSubtask.getName(),
                firstSubtask.getDescription(), firstSubtask.getStatus(), firstSubtask.getStartTime(),
                firstSubtask.getDuration());

        updatedSubtask.setId(firstSubtask.getId());
        taskManager.updateSubtask(updatedSubtask);

        List<Subtask> subtaskByEpic = taskManager.getSubtasksByEpic(epic);

        assertEquals(updatedSubtask, subtaskByEpic.getFirst(), "Сохранился неуктуальный Id подзадачи");
        assertEquals(1, subtaskByEpic.size(),
                "Сохранился неактуальный Id подзадачи вместе с актуальным");
        taskManager.removeSubtasks();
        subtaskByEpic = taskManager.getSubtasksByEpic(epic);
        assertEquals(0, subtaskByEpic.size(), "Сохранился неактуальный Id подзадачи после удаления");
    }

    @Test
    public void epicHasStatusNewIfAllSubtasksHasStatusNew() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        assertEquals(Status.NEW, taskManager.getEpic(epic.getId()).getStatus(),
                "Статус эпика не соответствует действительности");
    }

    @Test
    public void epicHasStatusDoneIfAllSubtasksHasStatusDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(firstSubtask.getEpicId(), firstSubtask.getName(),
                firstSubtask.getDescription(), Status.DONE, firstSubtask.getStartTime(), firstSubtask.getDuration()));
        taskManager.createSubtask(new Subtask(secondSubtask.getEpicId(), secondSubtask.getName(),
                secondSubtask.getDescription(), Status.DONE, secondSubtask.getStartTime(),
                secondSubtask.getDuration()));
        assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus(),
                "Статус эпика не соответствует действительности");
    }

    @Test
    public void epicHasStatusInProgressIfAllSubtasksHasStatusNewAndDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(firstSubtask.getEpicId(), firstSubtask.getName(),
                firstSubtask.getDescription(), Status.NEW, firstSubtask.getStartTime(), firstSubtask.getDuration()));
        taskManager.createSubtask(new Subtask(secondSubtask.getEpicId(), secondSubtask.getName(),
                secondSubtask.getDescription(), Status.DONE, secondSubtask.getStartTime(),
                secondSubtask.getDuration()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus(),
                "Статус эпика не соответствует действительности");
    }

    @Test
    public void epicHasStatusInProgressIfAllSubtasksHasStatusInProgress() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask(firstSubtask.getEpicId(), firstSubtask.getName(),
                firstSubtask.getDescription(), Status.IN_PROGRESS, firstSubtask.getStartTime(),
                firstSubtask.getDuration()));
        taskManager.createSubtask(new Subtask(secondSubtask.getEpicId(), secondSubtask.getName(),
                secondSubtask.getDescription(), Status.IN_PROGRESS, secondSubtask.getStartTime(),
                secondSubtask.getDuration()));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus(),
                "Статус эпика не соответствует действительности");
    }

    @Test
    public void cannotCreateSubtaskWithNonExistEpic() {
        Subtask subtaskWithExistEpic = taskManager.createSubtask(firstSubtask);

        assertNull(subtaskWithExistEpic, "Подзадача может существовать с несуществующим эпиком");
    }

    @Test
    public void subtaskCannotExistWithEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getSubtask(firstSubtask.getId()), "Подзакада может существовать без эпика");
    }

    @Test
    public void taskIntervalCannotMatch() {
        Task firstTaskWithInterval = new Task(task.getName(), task.getDescription(), task.getStatus(),
                LocalDateTime.now(), Duration.ofHours(1));
        Task secondTaskWithInterval = new Task(task.getName(), task.getDescription(), task.getStatus(),
                LocalDateTime.now(), Duration.ofHours(1));

        taskManager.createTask(firstTaskWithInterval);
        assertNull(taskManager.createTask(secondTaskWithInterval), "Задачи могут иметь одинаковые интервалы");
    }

    @Test
    public void taskIntervalCannotOverlap() {
        Task firstTaskWithInterval = new Task(task.getName(), task.getDescription(), task.getStatus(),
                LocalDateTime.now(), Duration.ofHours(1));
        Task secondTaskWithInterval = new Task(task.getName(), task.getDescription(), task.getStatus(),
                LocalDateTime.now().minusMinutes(30), Duration.ofHours(1));

        taskManager.createTask(firstTaskWithInterval);
        assertNull(taskManager.createTask(secondTaskWithInterval),
                "Задачи могут иметь пересекающиеся интервалы");
    }

    @Test
    public void managerCanDeleteTaskById() {
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTask(task.getId()), "Менеджер не создаёт указанную задачу");
        taskManager.removeTask(task.getId());
        assertNull(taskManager.getTask(task.getId()), "Менеджер не удаляет указанную задачу");
    }

    @Test
    public void managerCorrectlyHandleDeleteNonExistentTask() {
        assertDoesNotThrow(() -> taskManager.removeTask(task.getId()),
                "Менеджер некорректно обрабатывает удаление несуществующей задачи");
    }

    @Test
    public void managerCanDeleteEpicById() {
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Менеджер не создаёт указанный эпик");
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpic(epic.getId()), "Менеджер не удаляет указанный эпик");
    }

    @Test
    public void managerCorrectlyHandleDeleteNonExistentEpic() {
        assertDoesNotThrow(() -> taskManager.removeEpic(epic.getId()),
                "Менеджер некорректно обрабатывает удаление несуществующего эпика");
    }

    @Test
    public void managerCorrectlyHandleDeleteNonExistentSubtask() {
        assertDoesNotThrow(() -> taskManager.removeSubtask(firstSubtask.getId()),
                "Менеджер некорректно обрабатывает удаление несуществующей подзадачи");
    }

    @Test
    public void managerCanDeleteSubtaskById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);
        assertEquals(firstSubtask, taskManager.getSubtask(firstSubtask.getId()),
                "Менеджер не создаёт указанную подзадачу");
        taskManager.removeSubtask(firstSubtask.getId());
        assertNull(taskManager.getSubtask(firstSubtask.getId()), "Менеджер не удаляет указанную подзадачу");
    }

    @Test
    public void managerCanGetSubtasksByEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);

        List<Subtask> subtasksByEpic = new ArrayList<>();

        subtasksByEpic.add(firstSubtask);
        subtasksByEpic.add(secondSubtask);
        assertEquals(subtasksByEpic, taskManager.getSubtasksByEpic(epic),
                "Менеджер выдаёт некорректный список подзадач эпика");
    }

    @Test
    public void managerCorrectlyHandleGetSubtasksByNonExistentEpic() {
        assertDoesNotThrow(() -> taskManager.getSubtasksByEpic(epic),
                "Менеджер некорректно обрабатывает поиск подзадач у несуществующего эпика");
    }
}