public class SearchByTitle implements SearchStrategy{
    @Override
    public void search(String keyword) {
        System.out.println("Searching by title "+keyword);
    }
}
