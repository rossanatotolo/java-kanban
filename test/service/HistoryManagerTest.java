package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void addTask() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных;
    public void shouldTasksAddedToTheHistoryManagerRetainThePreviousVersion() {
        Task task = new Task("Новая задача", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test // Удаляемые подзадачи не должны хранить внутри себя старые id.
    public void shouldDeletedSubtasksNotStoreOldIdInsideThemselves() {
        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 3, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 10));
        historyManager.add(subTask1);
        SubTask subTask2 = new SubTask("Новая подзадача2", "Описание", Status.NEW, 4, 2, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 20));
        historyManager.add(subTask2);

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        historyManager.add(epic1);
        Epic epic2 = new Epic("Новый эпик2", "Описание");
        historyManager.add(epic2);

        historyManager.remove(1);
        List<Task> list = historyManager.getHistory();
        assertNotEquals(subTask1.getId(), list.get(1), "Дубликаты в листе");
    }

    @Test //удаление задач из начала
    public void shouldRemoveFirst() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5));
        historyManager.add(task1);
        Task task2 = new Task("Новая задача2", "Описание", Status.IN_PROGRESS, 2, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 10));
        historyManager.add(task2);
        Task task3 = new Task("Новая задача3", "Описание", Status.DONE, 3, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 20));
        historyManager.add(task3);

        historyManager.remove(task1.getId());
        assertEquals(List.of(task2, task3), historyManager.getHistory(), "Проверка пройдена");
    }

    @Test //удаление задач из середины
    public void shouldRemoveMiddle() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5));
        historyManager.add(task1);
        Task task2 = new Task("Новая задача2", "Описание", Status.IN_PROGRESS, 2, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 10));
        historyManager.add(task2);
        Task task3 = new Task("Новая задача3", "Описание", Status.DONE, 3, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 20));
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        assertEquals(List.of(task1, task3), historyManager.getHistory(), "Проверка пройдена");
    }

    @Test //удаление задач с конца
    public void shouldRemoveLast() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5));
        historyManager.add(task1);
        Task task2 = new Task("Новая задача2", "Описание", Status.IN_PROGRESS, 2, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 10));
        historyManager.add(task2);
        Task task3 = new Task("Новая задача3", "Описание", Status.DONE, 3, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 20));
        historyManager.add(task3);

        historyManager.remove(task3.getId());
        assertEquals(List.of(task1, task2), historyManager.getHistory(), "Проверка пройдена");
    }

    @Test //проверка порядка сохранения в историю
    public void shouldBeInMemoryHistoryManagerCantHaveDuplicate() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5));
        historyManager.add(task1);
        Task task2 = new Task("Новая задача2", "Описание", Status.IN_PROGRESS, 2, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 10));
        historyManager.add(task2);
        Task task3 = new Task("Новая задача3", "Описание", Status.DONE, 3, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 20));
        historyManager.add(task3);

        List<Task> list = historyManager.getHistory();

        assertEquals(task1, list.getFirst(), "Элементы сохранены в верном порядке");
        assertEquals(task3, list.getLast(), "Элементы сохранены в верном порядке");
    }
}