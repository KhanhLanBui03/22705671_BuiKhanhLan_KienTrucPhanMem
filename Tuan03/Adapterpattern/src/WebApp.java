public class WebApp {
    private final JsonService jsonService;

    public WebApp(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    public void printUserData() {
        System.out.println("User data (JSON):");
        System.out.println(jsonService.getJson("user"));
    }

    public void printProductData() {
        System.out.println("Product data (JSON):");
        System.out.println(jsonService.getJson("product"));
    }

    public void sendData(String jsonPayload) {
        System.out.println("Gửi dữ liệu JSON lên hệ thống XML:");
        System.out.println("Input JSON : " + jsonPayload);
        // Adapter tự lo chuyển đổi
        System.out.println("Output XML : " + jsonService.convertToJson(jsonPayload));
    }
}
