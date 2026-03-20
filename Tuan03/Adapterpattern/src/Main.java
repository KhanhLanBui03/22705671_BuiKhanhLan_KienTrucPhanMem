//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern: XML ↔ JSON ===\n");

        // Client chỉ cần biết JsonService
        XmlService xmlService = new XmlService();
        JsonService adapter = new XmlToJsonAdapter(xmlService);
        WebApp app = new WebApp(adapter);

        // XML → JSON (đọc từ hệ thống cũ)
        app.printUserData();
        System.out.println();
        app.printProductData();
        System.out.println();

        // JSON → XML (ghi sang hệ thống cũ)
        app.sendData("{\"name\": \"Tran Thi B\", \"email\": \"b@example.com\", \"age\": 28}");

        System.out.println("\n=== Demo trực tiếp XmlService ===");
        System.out.println(xmlService.getXml("user"));
    }
}