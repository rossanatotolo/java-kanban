package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test // Удаляемые подзадачи не должны хранить внутри себя старые id.
    public void shouldDeletedSubtasksNotStoreOldIdInsideThemselves() {
        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 3, 1);
        historyManager.add(subTask1);
        SubTask subTask2 = new SubTask("Новая подзадача2", "Описание", Status.NEW, 4, 2);
        historyManager.add(subTask2);

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        historyManager.add(epic1);
        Epic epic2 = new Epic("Новый эпик2", "Описание");
        historyManager.add(epic2);

        historyManager.remove(1);
        List<Task> list = historyManager.getHistory();

        assertNotEquals(subTask1.getId(), list.get(1), "Дубликаты в листе");
    }
}