package service;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static model.TypeTask.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() { //сохраняет текущее состояние менеджера в файл. Исключения вида IOException нужно отлавливать внутри метода и выкидывать собственное непроверяемое исключение ManagerSaveException
        List<String> allTasks = new ArrayList<>();
        if (tasks.size() > 0) {
            for (Task task : tasks.values()) {
                allTasks.add(toString(task));
            }
        }
        if (epics.size() > 0) {
            for (Epic epic : epics.values()) {
                allTasks.add(toString(epic));
            }
        }
        if (subTasks.size() > 0) {
            for (SubTask subTask : subTasks.values()) {
                allTasks.add(toString(subTask));
            }
        }
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,description,status,epic;");
            for (String line : allTasks) {
                fileWriter.write(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка " + e.getMessage());
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) throws IOException {  //восстанавливает данные менеджера из файла при запуске программы.
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String filetext = Files.readString(file.toPath());
        String[] lines = filetext.split(";");
        int countId = 1;
        if (lines.length > 1) {
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    continue;
                }
                String line = lines[i];
                String[] str = line.split(",");
                String type = str[1];
                if (type.equals(TypeTask.TASK.toString())) {
                    Task task = fromString((line));
                    fileBackedTaskManager.tasks.put(task.getId(), task);
                } else if (type.equals(TypeTask.EPIC.toString())) {
                    Epic epic = (Epic) fromString(line);
                    fileBackedTaskManager.epics.put(epic.getId(), epic);
                } else {
                    SubTask subTask = (SubTask) fromString(line);
                    fileBackedTaskManager.subTasks.put(subTask.getId(), subTask);
                }
            }
        }
        for (int i : fileBackedTaskManager.tasks.keySet()) {
            if (countId < i) {
                countId = i;
            }
        }
        for (int i : fileBackedTaskManager.epics.keySet()) {
            if (countId < i) {
                countId = i;
            }
        }
        for (SubTask subTask : fileBackedTaskManager.subTasks.values()) {
            Epic epic = fileBackedTaskManager.epics.get(subTask.getIdEpic());
            int id = subTask.getId();
            epic.addSubTasks(id);
            if (countId < id) {
                countId = id;
            }
        }
        fileBackedTaskManager.countId = countId;
        return fileBackedTaskManager;
    }


    private static String toString(Task task) { // преобразовывает задачу в строку
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + task.getIdEpic() + ";";
    }

    private static Task fromString(String value) { // преобразовывает строку в задачу
        String[] values = value.split(";");
        for (String str : values) {
            String[] tasks = str.split(",");
            int id = Integer.parseInt(tasks[0]);
            TypeTask type = TypeTask.valueOf(tasks[1]);
            String name = tasks[2];
            Status status = Status.valueOf(tasks[3]);
            String desc = tasks[4];
            if (type == TASK) {
                return new Task(name, desc, status, id);
            } else if (type == EPIC) {
                return new Epic(name, desc, status, id);
            } else if (type == SUBTASK) {
                return new SubTask(name, desc, status, Integer.parseInt(tasks[5]), id);
            }
        }
        return null;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }


    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }


    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }


    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public void clearSubTask() {
        super.clearSubTask();
        save();
    }
}
