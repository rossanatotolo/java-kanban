package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("test", "txt");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    public void shouldReadTasksInEmptyFile() throws IOException {
        FileBackedTaskManager f = FileBackedTaskManager.loadFromFile(file);
        List<Task> list1 = f.getAllTask();
        List<Epic> list2 = f.getAllEpic();
        List<SubTask> list3 = f.getAllSubTask();

        assertEquals(0, list1.size());
        assertEquals(0, list2.size());
        assertEquals(0, list3.size());
    }

    @Test
    public void shouldBeFileBackedManagerRestoreStateFromFile() throws IOException {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createTask(task1); //id = 1
        Epic epic1 = new Epic("Новый эпик1", "Описание"); //id 2
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 2, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5)); //id 3
        taskManager.createSubTask(subTask1);

        FileBackedTaskManager f2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(taskManager.getAllTask(), f2.getAllTask(), "Списки задач не совпадают");
        assertEquals(taskManager.getAllEpic(), f2.getAllEpic(), "Списки задач не совпадают");
        assertEquals(taskManager.getAllSubTask(), f2.getAllSubTask(), "Списки задач не совпадают");
    }

    @Test
    public void shouldWriteTasksInFile() throws IOException {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createTask(task1); //id = 1

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        taskManager.createEpic(epic1); //id = 2

        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 2, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5)); //id = 3
        taskManager.createSubTask(subTask1); //id = 3

        String fr = Files.readString(file.toPath());
        String[] lines = fr.split(";");

        assertEquals(4, lines.length, "Количество строк не совпадает с ожидаемым");
        assertEquals("id,type,name,description,status,epic,duration,startTime", lines[0], "Базовая строка не добавлена");
        assertEquals("1,TASK,Новая задача1,NEW,Описание,null,PT3M,2024-06-10T03:00", lines[1], "Задачи добавляются неверно");
    }

    @Test
    public void shouldWriteAndDeleteTasksInFileTest() throws IOException {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createTask(task1); //id = 1
        Task task2 = new Task("Новая задача2", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5));
        taskManager.createTask(task2); //id = 2
        taskManager.deleteTask(2);

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        taskManager.createEpic(epic1); //id = 3
        taskManager.clearEpic();

        FileBackedTaskManager f2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, taskManager.tasks.size(), "Удаление неверно");
        assertEquals(0, taskManager.epics.size(), "Удаление неверно");
    }

    @Test
    public void shouldCreateTaskAndSaveFromFile() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        taskManager.createTask(task1); //id = 1

        assertNotNull(taskManager.getHistory(), "Пусто");
        assertNotNull(taskManager.getAllTask(), "Пусто");
    }

    @Test
    public void shouldWriteAndReadFileAndCheckingForDurationTest() {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 2, 0));
        taskManager.createTask(task1); //id = 1

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        taskManager.createEpic(epic1); //id = 2

        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 2, Duration.ofMinutes(30), LocalDateTime.of(2024, 6, 10, 3, 0)); //id = 3
        taskManager.createSubTask(subTask1); //id = 3

        SubTask subTask2 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 2, Duration.ofMinutes(30), LocalDateTime.of(2024, 6, 10, 4, 0)); //id = 3
        taskManager.createSubTask(subTask2); //id = 4

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

        Epic epicTest = taskManager.getEpic(2);
        String startTimeEpic = epicTest.getStartTime().format(formatter);
        String endTimeEpic = epicTest.getEndTimeEpic().format(formatter);

        Duration durationEpic = epicTest.getDuration();
        LocalDateTime start = subTask1.getStartTime();
        LocalDateTime finish = subTask2.getEndTime();
        Duration duration = Duration.between(start, finish);

        assertEquals("10.06.2024_03:00", startTimeEpic, "Время начала эпика не совпадает");
        assertEquals("10.06.2024_04:30", endTimeEpic, "Время окончания эпика не совпадает");
        assertEquals(duration, durationEpic, "Продолжительность эпика не совпадает");
    }

    @Test
    public void shouldFileBackedTaskManagerRestoreFile() throws IOException {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 2, 0));
        taskManager.createTask(task1); //id = 1

        Task task2 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 2, 10));
        taskManager.createTask(task2); //id = 2

        Task task3 = new Task("Новая задача1", "Описание", Status.NEW, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 2, 20));
        taskManager.createTask(task3); //id = 3

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertNotEquals(taskManager, taskManager2, "Экземляры FileBackedTaskManager идентичны");
        assertEquals(taskManager.getAllTask(), taskManager2.getAllTask(), "Количество не совпадает");
        assertEquals(taskManager.getPrioritizedTasks(), taskManager2.getPrioritizedTasks());
    }
}
