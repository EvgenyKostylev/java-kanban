package manager;

public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {
    @Override
    protected InMemoryHistoryManager createHistoryManager() {
        return new InMemoryHistoryManager();
    }
}