public class TeamMember implements Observer {
    private final String name;
    private final String role;

    public TeamMember(String name, String role) {
        this.name = name;
        this.role = role;
    }

    @Override
    public void update(String event) {
        System.out.printf("  👤 %s (%s) nhận thông báo: %s%n", name, role, event);
    }
}
