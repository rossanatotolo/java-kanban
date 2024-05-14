package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    private TaskManager taskManager;

    @BeforeEach
    public void addTask() {
        taskManager = Managers.getDefault();
    }

    @Test
    //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    public void shouldClassUtilityAlwaysReturnInitializedAndGoodInstancesOfManagers() {
        assertNotNull(taskManager, "Экземляр класса не проинициализирован");
    }

    @Test //проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    public void shouldInMemoryTaskManagerAddTasksOfDifferentTypesAndCanFindThemById() {
        Task task = new Task("Новая задача", "Описание", Status.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("Новый эпик", "Описание");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Новая подзадача", "Описание", Status.NEW, 2);
        taskManager.createSubTask(subTask);

        assertEquals(1, taskManager.getAllTask().size(), "Количество задач не совпадает");
        assertEquals(1, taskManager.getAllEpic().size(), "Количество задач не совпадает");
        assertEquals(1, taskManager.getAllSubTask().size(), "Количество задач не совпадает");

        assertNotNull(taskManager.getTask(1), "Задача не найдена");
        assertNotNull(taskManager.getEpic(2), "Задача не найдена");
        assertNotNull(taskManager.getSubTask(3), "Задача не найдена");

        assertEquals(task, taskManager.getTask(1), "Задачи не совпадают");
        assertEquals(epic, taskManager.getEpic(2), "Задачи не совпадают");
        assertEquals(subTask, taskManager.getSubTask(3), "Задачи не совпадают");
    }

    @Test // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    public void shouldTasksIdDoNotConflictInMemoryTaskManager() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW);
        taskManager.createTask(task1);

        Task task2 = new Task("Новая задача2", "Описание", Status.IN_PROGRESS);
        taskManager.createTask(task2);

        Task task3 = new Task("Новая задача3", "Описание", Status.NEW, 2);
        taskManager.updateTask(task3);

        assertEquals(task2, task3, "Задачи не совпадают");
        assertEquals(2, taskManager.getAllTask().size(), "Количество задач не совпадает");
    }

    @Test // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер;
    public void shouldTasksUnchangedWhenAddingToInMemoryTaskManager() {
        Task task = new Task("Новая задача", "Описание", Status.NEW);
        taskManager.createTask(task);
        Task task1 = taskManager.getTask(1);

        assertEquals(task.getName(), task1.getName(), "Поля не совпадают");
        assertEquals(task.getDescription(), task1.getDescription(), "Поля не совпадают");
        assertEquals(task.getStatus(), task1.getStatus(), "Поля не совпадают");
        assertEquals(task.getId(), task1.getId(), "Поля не совпадают");
    }
}