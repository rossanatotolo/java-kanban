package model;

public class SubTask extends Task {
    private final int idEpic;

    public SubTask(String name, String description, Status status, int idEpic) { //для записи
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public SubTask(int id, String name, String description, Status status, int idEpic) { //перезапись
        super(name, description, status);
        this.idEpic = idEpic;
        setId(id);
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "idEpic=" + idEpic +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
