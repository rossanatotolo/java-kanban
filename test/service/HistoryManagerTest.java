package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryManagerTest {
    private HistoryManager historyManager;
    @BeforeEach
    public void addTask() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных;
    public void shouldTasksAddedToTheHistoryManagerRetainThePreviousVersion() {
    Task task = new Task("Новая задача", "Описание", Status.NEW);
    historyManager.add(task);

    final List<Task> history = historyManager.getHistory();

    assertNotNull(history, "История не пустая.");
    assertEquals(1, history.size(), "История не пустая.");
    }

}