package util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ManagersTest {
    private static TaskManager manager;

    @BeforeAll
    public static void beforeAll() {
        manager = Managers.getDefault();
    }

    @Test
    public void managersAlwaysReturnInitializedTaskManager() {
        assertInstanceOf(TaskManager.class, manager);
    }
}