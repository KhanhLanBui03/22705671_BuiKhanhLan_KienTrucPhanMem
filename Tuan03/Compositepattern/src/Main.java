//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Xây dựng cây thư mục
        Directory root = new Directory("root");

        Directory src = new Directory("src");
        src.add(new File("Main.java", 2_048));
        src.add(new File("Utils.java", 1_024));

        Directory test = new Directory("test");
        test.add(new File("MainTest.java", 512));

        Directory resources = new Directory("resources");
        resources.add(new File("config.properties", 256));
        resources.add(new File("banner.txt", 128));

        root.add(src);
        root.add(test);
        root.add(resources);
        root.add(new File("pom.xml", 4_096));

        // Hiển thị toàn bộ cây
        System.out.println("=== Cấu trúc thư mục ===");
        root.display("");

        // Truy cập kích thước từng phần
        System.out.println("\n=== Kích thước ===");
        System.out.printf("src/    : %,d bytes%n", src.getSize());
        System.out.printf("test/   : %,d bytes%n", test.getSize());
        System.out.printf("Tổng    : %,d bytes%n", root.getSize());
    }
}