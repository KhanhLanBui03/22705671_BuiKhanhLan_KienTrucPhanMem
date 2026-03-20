//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // ── Trường hợp 1: Cổ phiếu ───────────────────────────────
        System.out.println("=== Thị trường chứng khoán ===");

        StockMarket vnm = new StockMarket("VNM", 80_000);
        StockMarket fpt = new StockMarket("FPT", 120_000);

        Investor alice = new Investor("Alice", "VNM");
        Investor bob   = new Investor("Bob",   "FPT");
        Investor carol = new Investor("Carol", "VNM"); // theo dõi cả VNM

        vnm.addObserver(alice);
        vnm.addObserver(carol);
        fpt.addObserver(bob);
        fpt.addObserver(carol);

        vnm.setPrice(85_000);
        fpt.setPrice(115_000);
        vnm.setPrice(78_000);

        // Huỷ đăng ký
        vnm.removeObserver(carol);
        System.out.println("(Carol huỷ theo dõi VNM)");
        vnm.setPrice(90_000); // Carol không nhận được nữa

        // ── Trường hợp 2: Công việc dự án ────────────────────────
        System.out.println("\n=== Tiến độ dự án ===");

        ProjectTask task1 = new ProjectTask("Thiết kế database");
        ProjectTask task2 = new ProjectTask("Viết API đăng nhập");

        TeamMember dev1 = new TeamMember("Minh", "Developer");
        TeamMember dev2 = new TeamMember("Lan",  "Developer");
        TeamMember pm   = new TeamMember("Hùng", "PM");

        task1.addObserver(dev1);
        task1.addObserver(pm);
        task2.addObserver(dev2);
        task2.addObserver(pm);

        task1.setStatus("IN_PROGRESS");
        task2.setStatus("IN_PROGRESS");
        task1.setStatus("DONE");
        task2.setStatus("BLOCKED");
    }
}