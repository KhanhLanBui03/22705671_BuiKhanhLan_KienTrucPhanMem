import java.util.ArrayList;
import java.util.List;

public class ProjectTask implements Observable {
    private final String taskName;
    private String status; // TODO, IN_PROGRESS, DONE, BLOCKED
    private final List<Observer> observers = new ArrayList<>();

    public ProjectTask(String taskName) {
        this.taskName = taskName;
        this.status = "TODO";
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String event) {
        for (Observer o : observers) o.update(event);
    }

    public void setStatus(String newStatus) {
        String event = String.format("[Task: %s] %s → %s", taskName, status, newStatus);
        this.status = newStatus;
        notifyObservers(event);
    }

    public String getStatus() {
        return status;
    }
}
