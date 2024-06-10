package service;

import exception.TimeException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    //создать задачу
    @Test
    public void shouldCreateTask() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 1, 2, 59));
        Task task5 = new Task("Новая задача5", "Описание", Status.NEW, 2, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 1, 4, 29));
        taskManager.createTask(task1);
        taskManager.createTask(task5);

        Task t = taskManager.getTask(task1.getId());

        assertNotNull(t, "Задача не добавлена");
        assertEquals(task1, t, "Задачи разные");

        Task task2 = new Task("Новая задача2", "Описание", Status.NEW, 2, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 1, 3, 0));
        Task task3 = new Task("Новая задача3", "Описание", Status.NEW, 3, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 1, 3, 1));
        Task task4 = new Task("Новая задача4", "Описание", Status.NEW, 1, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 1, 4, 0));
        Task task6 = new Task("Новая задача6", "Описание", Status.NEW, 3, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 1, 4, 30));
        Task task7 = new Task("Новая задача7", "Описание", Status.NEW, 3, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 1, 4, 31));

        assertThrows(TimeException.class, () -> {
            taskManager.createTask(task2);
        }, "Задачи пересекаются");
        assertThrows(TimeException.class, () -> {
            taskManager.createTask(task3);
        }, "Задачи пересекаются");
        assertThrows(TimeException.class, () -> {
            taskManager.createTask(task4);
        }, "Задачи пересекаются");
        assertThrows(TimeException.class, () -> {
            taskManager.createTask(task6);
        }, "Задачи пересекаются");
        assertThrows(TimeException.class, () -> {
            taskManager.createTask(task7);
        }, "Задачи пересекаются");

        int size = taskManager.getAllTask().size();
        assertEquals(2, size, "Ошибка в интервалах");
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        Epic epic2 = new Epic("Новый эпик2", "Описание");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Epic e = taskManager.getEpic(epic1.getId());
        int size = taskManager.getAllEpic().size();

        assertNotNull(e, "Эпик не добавлен");
        assertEquals(epic1, e, "Эпики разные");
        assertEquals(2, size, "Количество эпиков не совпадает");
    }

    @Test
    public void shouldCreateSubTask() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(90), LocalDateTime.of(2024, 6, 10, 5, 0));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        List<Integer> idEpics = taskManager.getEpic(subTask1.getIdEpic()).getIdSubTasks();
        int number = idEpics.get(0);

        assertNotNull(subTask1, "Подзадача не добавлена");
        assertEquals(subTask2, taskManager.getSubTask(3), "Задачи разные");
        assertEquals(2, taskManager.getAllSubTask().size(), "Неверное добавление подзадач");
        assertEquals(2, number, "Id подзадачи неверно записывается в эпик, при создании");

        SubTask subTask3 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(30), LocalDateTime.of(2024, 6, 10, 3, 10));
        SubTask subTask4 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(130), LocalDateTime.of(2024, 6, 10, 4, 50));

        assertThrows(TimeException.class, () -> {
            taskManager.createSubTask(subTask3);
        }, "Задачи пересекаются");
        assertThrows(TimeException.class, () -> {
            taskManager.createSubTask(subTask4);
        }, "Задачи пересекаются");
        assertEquals(2, taskManager.getAllSubTask().size(), "Ошибка в интервалах");
    }

    //получить задачу по id
    @Test
    public void shouldGetTask() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 0));
        Task task2 = new Task("Новая задача2", "Описание", Status.NEW, 2, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(task1, taskManager.getTask(1), "Не удалось получить задачу по id");
        assertEquals(task2, taskManager.getTask(2), "Не удалось получить задачу по id");
    }


    @Test
    public void shouldGetSubTask() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertEquals(subTask1, taskManager.getSubTask(2), "Не удалось получить задачу по id");
        assertEquals(subTask2, taskManager.getSubTask(3), "Не удалось получить задачу по id");
    }

    @Test
    public void shouldGetEpic() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        Epic epic2 = new Epic("Новый эпик1", "Описание");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        assertEquals(epic1, taskManager.getEpic(1), "Не удалось получить задачу по id");
        assertEquals(epic2, taskManager.getEpic(2), "Не удалось получить задачу по id");
    }


    //удаление по id
    @Test
    public void shouldDeleteTask() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 0));
        Task task2 = new Task("Новая задача2", "Описание", Status.NEW, 2, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteTask(2);

        assertEquals(1, taskManager.getAllTask().size(), "Задача не удалилась");
    }


    @Test
    public void shouldDeleteEpic() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        Epic epic2 = new Epic("Новый эпик1", "Описание");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.deleteEpic(2);

        assertEquals(1, taskManager.getAllEpic().size(), "Задача не удалилась");
    }

    @Test
    public void shouldDeleteSubTask() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.deleteSubTask(2);

        assertEquals(1, taskManager.getAllSubTask().size(), "Задача не удалилась");
    }

    //удалить все задачи
    @Test
    public void shouldClearTask() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 0));
        Task task2 = new Task("Новая задача2", "Описание", Status.NEW, 2, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.clearTask();

        assertEquals(0, taskManager.getAllTask().size(), "Не все задачи удалились");
    }

    @Test
    public void shouldClearSubtask() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.clearSubTask();

        assertEquals(0, taskManager.getAllSubTask().size(), "Не все задачи удалились");
    }

    @Test
    public void shouldClearEpic() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        Epic epic2 = new Epic("Новый эпик1", "Описание");
        SubTask subTask2 = new SubTask("Новая подзадача", "Описание", Status.NEW, 3, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.clearEpic();

        assertEquals(0, taskManager.getAllEpic().size(), "Не все задачи удалились");
    }

    //обновить задачи
    @Test
    public void shouldUpdateTask() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 0));
        taskManager.createTask(task1);

        task1 = new Task("Новая задача2", "Описание", Status.IN_PROGRESS, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 0));

        assertEquals(Status.IN_PROGRESS, task1.getStatus(), "Задача не обновилась");
        assertEquals("Новая задача2", task1.getName(), "Задача не изменила поле name");
    }

    @Test
    public void shouldUpdateSubTask() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        subTask1 = new SubTask("Новая подзадача2", "Описание", Status.IN_PROGRESS, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));

        assertEquals(Status.IN_PROGRESS, subTask1.getStatus(), "Задача не обновилась");
        assertEquals("Новая подзадача2", subTask1.getName(), "Задача не изменила поле name");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        epic1 = new Epic("Новый эпик2", "Описание2");

        assertEquals("Новый эпик2", epic1.getName(), "Задача не изменила поле name");
        assertEquals("Описание2", epic1.getDescription(), "Задача не изменила поле description");
    }


    @Test
    public void shouldGetHistory() {
        List<Task> list1 = taskManager.getHistory();
        assertEquals(0, list1.size(), "История просмотров не пуста");

        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 0));
        Task task2 = new Task("Новая задача2", "Описание", Status.NEW, 2, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача", "Описание", Status.NEW, 3, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача", "Описание", Status.NEW, 3, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.getTask(1); //task1
        taskManager.getEpic(3); // epic1
        taskManager.getSubTask(5); //subtask2
        taskManager.getSubTask(4); //subTask1
        taskManager.getTask(1);//task1
        taskManager.getTask(2);//task2
        taskManager.getTask(1);//task1

        List<Task> list = taskManager.getHistory();
        assertEquals(5, list.size(), "Ошибка в истории просмотра задач");

        taskManager.deleteTask(1);
        taskManager.deleteSubTask(4);
        taskManager.deleteTask(2);

        assertEquals(epic1, list.get(0), "Id задачи и элемента листа не совпадают");
        assertEquals(subTask1, list.get(2), "Id задачи и элемента листа не совпадают");
        assertEquals(subTask2, list.get(1), "Id задачи и элемента листа не совпадают");
    }

    @Test
    public void shouldGetPrioritizedTasks() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 0));
        Task task2 = new Task("Новая задача2", "Описание", Status.NEW, 2, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 1, 2, 10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 3, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача2", "Описание", Status.NEW, 3, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        TreeSet<Task> treeSet = taskManager.getPrioritizedTasks();

        assertEquals(4, treeSet.size(), "Не удалось заполнить treeSet");
        assertTrue(subTask1.getStartTime().isBefore(subTask2.getStartTime()), "Задачи добавлены не в том порядке");
        assertEquals(epic1.getStartTime(), subTask1.getStartTime(), "Время начала не корректно");
        assertEquals(epic1.getEndTime(), subTask2.getEndTime(), "Время начала не корректно");
    }

    //расчет статусов эпика
    @Test
    public void shouldStatusEndIdEpic() {
        Epic epic1 = new Epic("Новый эпик1", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача2", "Описание", Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 10));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Новый эпик2", "Описание");
        SubTask subTask3 = new SubTask("Новая подзадача3", "Описание", Status.DONE, 4, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 20));
        SubTask subTask4 = new SubTask("Новая подзадача4", "Описание", Status.DONE, 4, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 30));
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);

        Epic epic3 = new Epic("Новый эпик3", "Описание");
        SubTask subTask5 = new SubTask("Новая подзадача5", "Описание", Status.NEW, 7, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 40));
        SubTask subTask6 = new SubTask("Новая подзадача6", "Описание", Status.DONE, 7, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 3, 50));
        taskManager.createEpic(epic3);
        taskManager.createSubTask(subTask5);
        taskManager.createSubTask(subTask6);

        Epic epic4 = new Epic("Новый эпик4", "Описание");
        SubTask subTask7 = new SubTask("Новая подзадача7", "Описание", Status.IN_PROGRESS, 10, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 4, 40));
        SubTask subTask8 = new SubTask("Новая подзадача8", "Описание", Status.IN_PROGRESS, 10, Duration.ofMinutes(5), LocalDateTime.of(2024, 6, 10, 4, 50));
        taskManager.createEpic(epic4);
        taskManager.createSubTask(subTask7);
        taskManager.createSubTask(subTask8);

        assertEquals(Status.NEW, epic1.getStatus(), "Статус не рассчитался");
        assertEquals(Status.DONE, epic2.getStatus(), "Статус не рассчитался");
        assertEquals(Status.IN_PROGRESS, epic3.getStatus(), "Статус не рассчитался");
        assertEquals(Status.IN_PROGRESS, epic4.getStatus(), "Статус не рассчитался");

        assertEquals(epic1.getId(), subTask2.getIdEpic(), "Id эпика не совпал");
        assertEquals(epic4.getId(), subTask7.getIdEpic(), "Id эпика не совпал");
    }
}