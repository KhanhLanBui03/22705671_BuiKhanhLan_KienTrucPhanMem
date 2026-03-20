import java.util.ArrayList;
import java.util.List;

public class StockMarket implements Observable {

    private final String stockName;
    private double price;
    private final List<Observer> observers = new ArrayList<>();

    public StockMarket(String stockName, double initialPrice) {
        this.stockName = stockName;
        this.price = initialPrice;
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

    public void setPrice(double newPrice) {
        double old = this.price;
        this.price = newPrice;
        String direction = newPrice > old ? "▲" : "▼";
        String event = String.format("[%s] %s %.2f → %.2f (%s%.2f%%)",
                stockName, direction, old, newPrice,
                newPrice > old ? "+" : "", (newPrice - old) / old * 100);
        notifyObservers(event);
    }

    public double getPrice() {
        return price;
    }
}
