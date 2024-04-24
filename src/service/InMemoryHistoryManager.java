package service;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history = new ArrayList<>();
    private final static int SIZE = 10; // да я что-то упустила этот момент(

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == SIZE) {
                history.remove(0);
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }


}
