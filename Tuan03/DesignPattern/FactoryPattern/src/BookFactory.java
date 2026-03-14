public class BookFactory {
    public static Book createBook(String type) {
        return switch (type) {
            case "EBook" -> new EBook();
            case "PaperBook" -> new PaperBook();
            case "AudioBook" -> new AudioBook();
            default -> null;
        };

    }
}
