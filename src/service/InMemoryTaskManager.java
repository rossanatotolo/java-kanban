package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int countId = 0;

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    //Блок тасок
    @Override
    public List<Task> getAllTask() {     //методы Task (6 шт);
        return new ArrayList<>(tasks.values());
    }
    @Override
    public void clearTask() {
        tasks.clear();
    }
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }
    @Override
    public void createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
    }
    @Override
    public void updateTask(Task task) {
       int id = task.getId();
       if (tasks.containsKey(id)) {
           tasks.put(id, task);
       }
    }
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }


    //блок эпик
    @Override
    public List<Epic> getAllEpic() {  // методы Epic (6 шт);
        return new ArrayList<>(epics.values());
    }
    @Override
    public void clearEpic() {
        epics.clear();
        subTasks.clear();
    }
    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }
    @Override
    public void createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
    }
    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic != null) {
            oldEpic.setName(epic.getName());
            oldEpic.setDescription(epic.getDescription());
        }
    }
    @Override
    public void deleteEpic(int id) {
        for (int idSubTask: epics.get(id).getIdSubTasks()) {
            subTasks.remove(idSubTask);
        }
        epics.remove(id);
    }

    //блок сабтасок
    @Override
    public List<SubTask> getAllSubTask() { //методы subTask (6 шт);
        return new ArrayList<>(subTasks.values());
    }
    @Override
    public void clearSubTask() {
        for (Epic epic: epics.values()) {
            epic.clearSubTasks();
            calculateStatus(epic);
        }
        subTasks.clear();
    }
    @Override
    public SubTask getSubTask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }
    @Override
    public void createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getIdEpic());
        if (epic != null) {
            int id = generateId();
            subTasks.put(id, subTask);
            subTask.setId(id);
            epic.addSubTasks(id);
            calculateStatus(epic);
        }
    }
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            final Epic epic = epics.get(subTask.getIdEpic());
            calculateStatus(epic);
        }
    }
    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        final Epic epic = epics.get(subTask.getIdEpic());
        epic.removeSubTasks(id);
        calculateStatus(epic);
    }

    // доп.методы
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
           epic.setStatus(Status.IN_PROGRESS);
       }
    }


    private int generateId() {   //генерация id;
        return ++countId;
    }
}

