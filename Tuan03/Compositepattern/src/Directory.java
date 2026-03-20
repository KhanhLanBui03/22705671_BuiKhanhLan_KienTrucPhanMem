import java.util.ArrayList;
import java.util.List;

public class Directory implements FileSystemComponent {
    private final String name;
    private final List<FileSystemComponent> children = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    public void add(FileSystemComponent component) {
        children.add(component);
    }

    public void remove(FileSystemComponent component) {
        children.remove(component);
    }

    @Override public String getName() { return name; }

    @Override
    public long getSize() {
        // Tổng kích thước = tổng kích thước tất cả con
        return children.stream().mapToLong(FileSystemComponent::getSize).sum();
    }

    @Override
    public void display(String indent) {
        System.out.printf("%s📁 %s/ (%,d bytes)%n", indent, name, getSize());
        for (FileSystemComponent child : children) {
            child.display(indent + "  ");
        }
    }
}
