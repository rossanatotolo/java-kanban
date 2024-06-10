package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getHistory();

    List<Task> getAllTask();

    void clearTask();

    Task getTask(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    List<Epic> getAllEpic();

    void clearEpic();

    Epic getEpic(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    void createSubTask(SubTask subTask);

    List<SubTask> getAllSubTask();

    void clearSubTask();

    SubTask getSubTask(int id);

    void updateSubTask(SubTask subTask);

    void deleteSubTask(int id);

    TreeSet<Task> getPrioritizedTasks();
}


