package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private TaskManager taskManager;

    @BeforeEach
    public void addTask() {
        taskManager = Managers.getDefault();
    }

    @Test // проверьте, что наследники класса Task равны друг другу, если равен их id;
    public void shouldExtendsTasksEqualsIfTheirIdAreEquals() {
        Epic epic = new Epic("Новый эпик", "Описание");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1);
        taskManager.createSubTask(subTask1);
        final int id = subTask1.getId();

        SubTask subTask2 = new SubTask("Новая подзадача2", "Описание2", Status.IN_PROGRESS, 1, id);
        taskManager.updateSubTask(subTask2);

        assertEquals(subTask1, subTask2, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getAllSubTask();

        assertNotNull(subTask1, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask2, subTasks.get(0), "Задачи не совпадают.");
    }
}
