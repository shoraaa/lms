package app;
import java.util.Calendar;

public class UserHistory {
    protected String docName;
    protected int quantity;
    protected Calendar borrowedDay;
    protected Calendar returnDay;
    protected Calendar deadline;

    public UserHistory() {
        this.docName = "";
        this.quantity = 0;
    }

    public UserHistory(String docName, int num) {
        this.docName = docName;
        this.quantity = num;
        this.borrowedDay = Calendar.getInstance();
        this.deadline = this.borrowedDay;
        this.deadline.add(Calendar.DAY_OF_MONTH, 30);
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
        System.out.print(this.docName + " " + this.quantity + " ");
        showCalendar(borrowedDay);
        System.out.print(" ");
        showCalendar(deadline);
        System.out.print(" ");
        showCalendar(returnDay);
        System.out.println(".");
    }
}
