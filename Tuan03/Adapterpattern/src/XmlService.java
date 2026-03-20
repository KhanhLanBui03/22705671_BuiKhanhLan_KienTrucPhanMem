import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlService {
    /**
     * Lấy dữ liệu dạng XML theo key
     */
    public String getXml(String key) {
        // Giả lập trả về XML từ hệ thống cũ
        return switch (key) {
            case "user" -> """
                    <user>
                      <id>1001</id>
                      <name>Nguyen Van A</name>
                      <email>vana@example.com</email>
                      <role>admin</role>
                    </user>""";
            case "product" -> """
                    <product>
                      <id>P-999</id>
                      <name>Laptop XZ</name>
                      <price>25000000</price>
                      <stock>50</stock>
                    </product>""";
            default -> "<error><message>Key not found</message></error>";
        };
    }

    /**
     * Chuyển JSON string sang XML
     */
    public String convertToXml(String jsonData) {
        // Đơn giản hoá: chuyển flat JSON object sang XML
        StringBuilder xml = new StringBuilder("<root>\n");
        Pattern kv = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"?([^\",}]+)\"?");
        Matcher m = kv.matcher(jsonData);
        while (m.find()) {
            xml.append("  <").append(m.group(1)).append(">")
                    .append(m.group(2).strip())
                    .append("</").append(m.group(1)).append(">\n");
        }
        xml.append("</root>");
        return xml.toString();
    }
}
