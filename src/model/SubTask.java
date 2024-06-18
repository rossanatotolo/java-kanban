package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int idEpic;

    public SubTask(String name, String description, Status status, int idEpic, Duration duration, LocalDateTime startTime) { //для записи
        super(name, description, status, duration, startTime);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Status status, int idEpic, int id, Duration duration, LocalDateTime startTime) { //перезапись
        super(name, description, status, id, duration, startTime);
        this.idEpic = idEpic;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", idEpic=" + idEpic + '\'' +
                ", id=" + id + '\'' +
                '}';
    }
}
