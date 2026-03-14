public class ExtendTimeBorrow extends BorrowDecorator {

    public ExtendTimeBorrow(Borrow borrow){
        super(borrow);
    }

    public void borrow(){
        borrow.borrow();
        System.out.println("Extended borrowing time");
    }

}