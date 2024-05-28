package model;

public class SubTask extends Task {
    private final int idEpic;

    public SubTask(String name, String description, Status status, int idEpic) { //для записи
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Status status, int idEpic, int id) { //перезапись
        super(name, description, status, id);
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
