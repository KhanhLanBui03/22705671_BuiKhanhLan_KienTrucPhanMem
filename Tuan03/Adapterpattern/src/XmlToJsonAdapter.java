import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlToJsonAdapter implements JsonService {
    private final XmlService xmlService;

    public XmlToJsonAdapter(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    /**
     * Lấy dữ liệu XML từ Adaptee rồi chuyển sang JSON
     */
    @Override
    public String getJson(String key) {
        String xml = xmlService.getXml(key);
        return xmlToJson(xml);
    }

    /**
     * Nhận JSON, chuyển sang XML nhờ Adaptee, rồi trả JSON đã round-trip
     */
    @Override
    public String convertToJson(String xmlData) {
        return xmlToJson(xmlData);
    }

    // ── private helpers ──────────────────────────────────────────

    private String xmlToJson(String xml) {
        StringBuilder json = new StringBuilder("{\n");
        Pattern tagPair = Pattern.compile("<(\\w+)>([^<]*)</\\1>");
        Matcher m = tagPair.matcher(xml);
        boolean first = true;

        while (m.find()) {
            String tag = m.group(1);
            String value = m.group(2).strip();

            // Bỏ qua root tag wrapper nếu có
            if (tag.equals("root") || tag.equals("user") ||
                    tag.equals("product") || tag.equals("error")) continue;

            if (!first) json.append(",\n");
            json.append("  \"").append(tag).append("\": ");

            // Nếu là số thì không thêm dấu nháy
            if (value.matches("-?\\d+(\\.\\d+)?")) {
                json.append(value);
            } else {
                json.append("\"").append(value).append("\"");
            }
            first = false;
        }
        json.append("\n}");
        return json.toString();
    }
}
