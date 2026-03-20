public class Investor implements Observer {
    private final String name;
    private final String watchedStock;

    public Investor(String name, String watchedStock) {
        this.name         = name;
        this.watchedStock = watchedStock;
    }

    @Override
    public void update(String event) {
        if (event.contains(watchedStock)) {
            System.out.printf("  👤 Nhà đầu tư [%s] nhận thông báo: %s%n", name, event);
        }
    }
}
