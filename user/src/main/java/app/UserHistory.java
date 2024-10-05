package app;
import java.util.Calendar;

public class UserHistory {
    protected String DocName;
    protected int quantity;
    protected Calendar BorrowedDay;
    protected Calendar ReturnDay;
    protected Calendar Deadline;

    public UserHistory() {
        this.DocName = "";
        this.quantity = 0;
    }

    public UserHistory(String DN, int num) {
        this.DocName = DN;
        this.quantity = num;
        this.BorrowedDay = Calendar.getInstance();
        this.Deadline = this.BorrowedDay;
        this.Deadline.add(Calendar.DAY_OF_MONTH, 30);
    }

    public static void showCalendar(Calendar c) {
        if (c == null) {
            System.out.print("Books have not been returned!");
            return;
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
 
        System.out.print(day + "-" + (month + 1) + "-" + year);
    }

    public void print() {
        System.out.print(this.DocName + " " + this.quantity + " ");
        showCalendar(BorrowedDay);
        System.out.print(" ");
        showCalendar(Deadline);
        System.out.print(" ");
        showCalendar(ReturnDay);
        System.out.println(".");
    }
}
