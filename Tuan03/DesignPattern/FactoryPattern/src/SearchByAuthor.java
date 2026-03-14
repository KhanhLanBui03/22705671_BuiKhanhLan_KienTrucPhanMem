public class SearchByAuthor implements SearchStrategy{
    @Override
    public void search(String keyword) {
        System.out.println("Searching by Author: "+keyword);
    }
}
