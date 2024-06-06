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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    private static File file;
    private static FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("test", "txt");
        fileBackedTaskManager = new FileBackedTaskManager(file);
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
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW);
        fileBackedTaskManager.createTask(task1); //id = 1
        Epic epic1 = new Epic("Новый эпик1", "Описание"); //id 2
        fileBackedTaskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 2); //id 3
        fileBackedTaskManager.createSubTask(subTask1);

        FileBackedTaskManager f2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(fileBackedTaskManager.getAllTask(), f2.getAllTask(), "Списки задач не совпадают");
        assertEquals(fileBackedTaskManager.getAllEpic(), f2.getAllEpic(), "Списки задач не совпадают");
        assertEquals(fileBackedTaskManager.getAllSubTask(), f2.getAllSubTask(), "Списки задач не совпадают");

    }

    @Test
    public void shouldWriteTasksInFile() throws IOException {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW);
        fileBackedTaskManager.createTask(task1); //id = 1

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        fileBackedTaskManager.createEpic(epic1); //id = 2

        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 2); //id = 3
        fileBackedTaskManager.createSubTask(subTask1);

        String fr = Files.readString(file.toPath());
        String[] lines = fr.split(";");

        assertEquals(4, lines.length, "Количество строк не совпадает с ожидаемым");
        assertEquals("id,type,name,description,status,epic", lines[0], "Базовая строка не добавлена");
        assertEquals("1,TASK,Новая задача1,NEW,Описание,null", lines[1], "Задачи добавляются неверно");
    }

    @Test
    public void shouldWriteAndDeleteTasksInFileTest() throws IOException {
        Task task1 = new Task("Новая задача1", "Описание", Status.NEW);
        fileBackedTaskManager.createTask(task1); //id = 1
        Task task2 = new Task("Новая задача2", "Описание", Status.NEW);
        fileBackedTaskManager.createTask(task2); //id = 2
        fileBackedTaskManager.deleteTask(2);

        Epic epic1 = new Epic("Новый эпик1", "Описание");
        fileBackedTaskManager.createEpic(epic1); //id = 3
        fileBackedTaskManager.clearEpic();

        FileBackedTaskManager f2 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, fileBackedTaskManager.tasks.size(), "Удаление неверно");
        assertEquals(0, fileBackedTaskManager.epics.size(), "Удаление неверно");
    }
}
