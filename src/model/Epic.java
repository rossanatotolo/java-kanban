package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        setName(name);
        setDescription(description);
        setStatus(Status.NEW);
    }

    public Epic(String name, String description, Status status, int id, Duration duration, LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public Integer getIdEpic() {
        return super.getIdEpic();
    }

    public List<Integer> getIdSubTasks() {
        return subTasks;
    }

    public void addSubTasks(int id) {
        subTasks.add(id);
    }

    public void removeSubTasks(Integer id) {
        subTasks.remove(id);
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    public LocalDateTime getEndTimeEpic() {
        return endTime;
    }

    public void setEndTimeEpic(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDurationEpic(Duration duration) {
        super.setDuration(duration);
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


