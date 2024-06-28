package service;

import exception.NotFoundException;
import exception.TimeException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected int countId = 0;
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public Task getTask(int id) throws NotFoundException {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        }
        throw new NotFoundException("Задача с id = " + id + ", не существует.");
    }

    @Override
    public void createTask(Task task) {
        if (!isTaskOverlap(task)) {
            int id = generateId();
            task.setId(id);
            tasks.put(id, task);
        } else {
            throw new TimeException("Совпадает промежуток времени с другими задачами.");
        }
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            Task taskOld = tasks.remove(id);
            if (!isTaskOverlap(task)) {
                tasks.put(id, task);
            } else {
                tasks.put(id, taskOld);
                throw new TimeException("Совпадает промежуток времени с другими задачами.");
            }
        }
    }

    @Override
    public void deleteTask(int id) throws NotFoundException {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        } else {
            throw new NotFoundException("Задача с id = " + id + ", не существует.");
        }
    }

    //блок эпик
    @Override
    public List<Epic> getAllEpic() {  // методы Epic (6 шт);
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpic() {
        epics.keySet().forEach(historyManager::remove);
        subTasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpic(int id) throws NotFoundException {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        throw new NotFoundException("Задача с id = " + id + ", не существует.");
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
    public void deleteEpic(int id) throws NotFoundException {
        if (epics.containsKey(id)) {
            for (int idSubTask : epics.get(id).getIdSubTasks()) {
                historyManager.remove(idSubTask);
                subTasks.remove(idSubTask);

            }
            historyManager.remove(id);
            epics.remove(id);
        } else {
            throw new NotFoundException("Задача с id = " + id + ", не существует.");
        }
    }

    //блок сабтасок
    @Override
    public List<SubTask> getAllSubTask() { //методы subTask (6 шт);
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void clearSubTask() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            calculateStatus(epic);
        }
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.clear();
    }

    @Override
    public SubTask getSubTask(int id) throws NotFoundException {
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        }
        throw new NotFoundException("Задача с id = " + id + ", не существует.");
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (!isTaskOverlap(subTask)) {
            Epic epic = epics.get(subTask.getIdEpic());
            if (epic != null) {
                int id = generateId();
                subTask.setId(id);
                subTasks.put(id, subTask);
                epic.addSubTasks(id);
                updateEpicInfo(epic);
            }
        } else {
            throw new TimeException("Совпадает промежуток времени с другими задачами.");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int id = subTask.getId();
        final Epic epic = epics.get(subTask.getIdEpic());
        if (subTasks.containsKey(id)) {
            SubTask subTaskOld = subTasks.remove(id);
            epic.removeSubTasks(id);
            updateEpicInfo(epic);
            if (!isTaskOverlap(subTask)) {
                subTasks.put(id, subTask);
                updateEpicInfo(epic);
            } else {
                subTasks.put(id, subTaskOld);
                updateEpicInfo(epic);
                throw new TimeException("Совпадает промежуток времени с другими задачами.");
            }
        }
    }

    @Override
    public void deleteSubTask(int id) throws NotFoundException {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.remove(id);
            final Epic epic = epics.get(subTask.getIdEpic());
            historyManager.remove(id);
            epic.removeSubTasks(id);
            updateEpicInfo(epic);
        } else {
            throw new NotFoundException("Задача с id = " + id + ", не существует.");
        }
    }

    // доп.методы
    private void calculateStatus(Epic epic) {  //определение статуса;
        int countStatusNew = 0;
        int countStatusDone = 0;
        if (epic.getIdSubTasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (int idSubTask : epic.getIdSubTasks()) {
                Status status = subTasks.get(idSubTask).getStatus();
                if (status == Status.IN_PROGRESS) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                } else if (status == Status.NEW) {
                    countStatusNew++;
                } else {
                    countStatusDone++;
                }
            }
            if ((countStatusDone == epic.getIdSubTasks().size()) && countStatusDone != 0) {
                epic.setStatus(Status.DONE);
            } else if (countStatusNew == epic.getIdSubTasks().size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public boolean isTaskOverlap(Task task) {
        for (Task t : getPrioritizedTasks()) {
            if ((task.getStartTime().isBefore(t.getStartTime()) || task.getStartTime().equals(t.getStartTime()) ||
                    task.getStartTime().isAfter(t.getStartTime()) && (task.getStartTime().isBefore(t.getEndTime()))) &&
                    (task.getEndTime().equals(t.getEndTime()) ||
                            task.getEndTime().isAfter(t.getEndTime()) || (task.getEndTime().isBefore(t.getEndTime())
                            && task.getEndTime().isAfter(t.getStartTime())))) {
                return true;
            }
        }
        return false;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        tasks.values().stream()
                .filter(Objects::nonNull)
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::add);

        subTasks.values().stream()
                .filter(Objects::nonNull)
                .filter(subTask -> subTask.getStartTime() != null)
                .forEach(prioritizedTasks::add);
        return prioritizedTasks;
    }

    private int generateId() {   //генерация id;
        return ++countId;
    }

    protected void setStartTimeEpic(Epic epic) {
        epic.getIdSubTasks().stream()
                .map(subTasks::get)
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .ifPresent(epic::setStartTime);
    }

    protected void setEndTimeEpic(Epic epic) {
        epic.getIdSubTasks().stream()
                .map(subTasks::get)
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo)
                .ifPresent(epic::setEndTimeEpic);
    }

    protected void setDuration(Epic epic) {
        if (epic.getIdSubTasks().size() == 0) {
            epic.setDurationEpic(null);
        } else {
            Duration duration = Duration.between(epic.getStartTime(), epic.getEndTimeEpic());
            epic.setDurationEpic(duration);
        }
    }

    protected void updateEpicInfo(Epic epic) {
        calculateStatus(epic);
        setStartTimeEpic(epic);
        setEndTimeEpic(epic);
        setDuration(epic);
    }
}

