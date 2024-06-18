package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    public void addTask() {
        taskManager = Managers.getDefault();
    }

    @Test //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    public void shouldTwoTasksEqualsIfTheirIdAreEquals() {
        Task task1 = new Task("Новая задача", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createTask(task1);
        final int id = task1.getId();

        Task task2 = new Task("Новая задача2", "Описание2", Status.IN_PROGRESS, id, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.updateTask(task2);

        assertNotNull(task2, "Задача не найдена");
        assertEquals(task1, task2, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    //С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
    public void shouldSettersMethodTest() {
        Task task1 = new Task("Новая задача", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createTask(task1);
        task1.setName("testName");
        task1.setDescription("testDescrp");
        task1.setStatus(Status.NEW);
        task1.setId(111);

        assertEquals("testName", task1.getName(), "Изменения сохранены");
        assertEquals("testDescrp", task1.getDescription(), "Изменения сохранены");
        assertEquals(Status.NEW, task1.getStatus(), "Изменения сохранены");
        assertEquals(111, task1.getId(), "Изменения сохранены");
    }
}