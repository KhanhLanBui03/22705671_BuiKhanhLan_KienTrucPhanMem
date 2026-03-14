import java.util.ArrayList;
import java.util.List;

public class LibraryNotification {

    private List<Observer> observers = new ArrayList<>();

    public void subscribe(Observer o){
        observers.add(o);
    }

    public void notifyUsers(String message){

        for(Observer o : observers){
            o.update(message);
        }

    }

}