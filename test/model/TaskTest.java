package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
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
        Task task1 = new Task("Новая задача", "Описание", Status.NEW);
        taskManager.createTask(task1);
        final int id = task1.getId();

        Task task2 = new Task("Новая задача2", "Описание2", Status.IN_PROGRESS, id);
        taskManager.updateTask(task2);

        assertNotNull(task2, "Задача не найдена");
        assertEquals(task1, task2, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

}