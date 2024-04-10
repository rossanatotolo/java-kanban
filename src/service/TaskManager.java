package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>(); //почитала про Мару, вроде бы достаточно только заменить тут в полях(переживала, что код сломается))
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int countId = 0;  // счетчик задает идентификатор, для всех задач!!


    public List<Task> getAll() {     //методы Task (6 шт);
        return new ArrayList<>(tasks.values());
    }
    public void clear() {
        tasks.clear();
    }

    public Task get(int id) {
        return tasks.get(id);
    }

    public void create(Task task) {
        int id = generateId();
        tasks.put(id, task);
        task.setId(id);
    }

    public void update(Task task) {
       int id = task.getId();
       if (tasks.containsKey(id)) {
           tasks.put(id, task);
       }
    }
    public void delete(int id) {
        tasks.remove(id);
    }



    public List<Task> getAllEpics() {  // методы Epic (6 шт);
        return new ArrayList<>(epics.values());
    }
    public void clearEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Epic getEpics(int id) {
        return epics.get(id);
    }

    public void createEpics(Epic epic) {
        int id = generateId();
        epics.put(id, epic);
        epic.setId(id);
    }

    public void updateEpics(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic != null) {
            oldEpic.setName(epic.getName());
            oldEpic.setDescription(epic.getDescription());
        }
    }

    public void deleteEpics(int id) {
        for (int idSubTask: epics.get(id).getIdSubTasks()) {
            subTasks.remove(idSubTask);
        }
        epics.remove(id);
    }



    public List<Task> getAllSubTask() { //методы subTask (6 шт);
        return new ArrayList<>(subTasks.values());
    }

    public void clearSubTasks() {
        for (Epic epic: epics.values()) {
            epic.clearSubTasks();
            calculateStatus(epic);
        }
        subTasks.clear();
    }

    public SubTask getSubTasks(int id) {
        return subTasks.get(id);
    }

    public void createSubTasks(SubTask subTask) {
        Epic epic = epics.get(subTask.getIdEpic());
        if (epic != null) {
            int id = generateId();
            subTasks.put(id, subTask);
            subTask.setId(id);
            epic.addSubTasks(id);
            calculateStatus(epic);
        }
    }

    public void updateSubTasks(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            final Epic epic = epics.get(subTask.getIdEpic());
            calculateStatus(epic);
        }
    }

    public void deleteSubTasks(int id) {
        SubTask subTask = subTasks.remove(id);
        Epic epic = epics.get(subTask.getIdEpic());
        epic.removeSubTasks(id);
        calculateStatus(epic);
    }


    private void calculateStatus(Epic epic) {  //определение статуса;
       int countStatusNew = 0;
       int countStatusDone = 0;

       for (int idSubTask: epic.getIdSubTasks()) {
           Status status = subTasks.get(idSubTask).getStatus();
           if (status == Status.IN_PROGRESS) {
              epic.setStatus(Status.IN_PROGRESS);
              return;
           } else if (status == Status.NEW) {
               countStatusNew++;
           } else {
               countStatusDone++;;
           }
       }
       if ((countStatusDone == epic.getIdSubTasks().size()) && countStatusDone != 0) {
           epic.setStatus(Status.DONE);
       } else {
           epic.setStatus(Status.NEW);
       }
    }


    private int generateId() {   //генерация id;
        return ++countId;
    }
}

