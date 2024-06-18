package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private TaskManager taskManager;

    @BeforeEach
    public void addTask() {
        taskManager = Managers.getDefault();
    }

    @Test
        //Внутри эпиков не должно оставаться неактуальных id подзадач
    void shouldBeNoIrrelevantSubtaskIdInsideTheEpics() {
        Epic epic = new Epic("Новый эпик", "Описание");
        SubTask subTask1 = new SubTask("Новая подзадача1", "Описание", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 0));
        SubTask subTask2 = new SubTask("Новая подзадача2", "Описание", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 5));
        SubTask subTask3 = new SubTask("Новая подзадача3", "Описание", Status.NEW, 1, Duration.ofMinutes(3), LocalDateTime.of(2024, 6, 10, 3, 10));

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteSubTask(2);

        List<Integer> list = epic.getIdSubTasks();
        assertEquals(2, list.size(), "Задачи неактуальные.");

        taskManager.clearSubTask();
        assertEquals(0, list.size(), "Неверное количество");
    }
}