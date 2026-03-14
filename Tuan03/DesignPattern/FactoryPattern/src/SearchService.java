public class SearchService {

    private SearchStrategy strategy;

    public void setStrategy(SearchStrategy strategy){
        this.strategy = strategy;
    }

    public void search(String keyword){
        strategy.search(keyword);
    }

}
