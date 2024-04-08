package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();


    public Epic(String name, String description) {
        setName(name);
        setDescription(description);
        setStatus(Status.NEW);
    }

    public List<Integer> getIdSubTasks() {
        return subTasks;
    }

    public void addSubTasks(int id) {
        subTasks.add(id);
    }

    public void removeSubTasks(int id) {
        subTasks.remove(id);
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}


