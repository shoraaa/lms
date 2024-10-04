public class UserHistory {
    protected String DocName;
    protected int quantity;
    protected Date BorrowedDay;
    protected Date ReturnDay;
    protected Date Deadline;

    public UserHistory(String DN, int num, Date br) {
        this.DocName = DN;
        this.quantity = num;
        this.BorrowedDay = br;
        this.Deadline = br.getDl();
    }

    public void print() {
        System.out.print(DocName + " " + quantity + " " + BorrowedDay + " " + Deadline + " " + ReturnDay);
    }
}
