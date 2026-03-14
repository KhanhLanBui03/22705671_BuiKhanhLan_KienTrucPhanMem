public class Main {

    public static void main(String[] args){

        Library library = Library.getInstance();

        Book book = BookFactory.createBook("EBook");
        book.read();

        SearchService service = new SearchService();
        service.setStrategy(new SearchByTitle());
        service.search("Java");

        LibraryNotification notification = new LibraryNotification();
        notification.subscribe(new User("Lan"));
        notification.notifyUsers("New book available!");

        Borrow borrow =
                new ExtendTimeBorrow(new BasicBorrow());

        borrow.borrow();

    }

}