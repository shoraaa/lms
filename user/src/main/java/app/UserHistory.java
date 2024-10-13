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

    public static String showCalendar(Calendar c) {
        if (c == null) {
            return "Books have not been returned!";
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
 
        return (" " + day + "-" + (month + 1) + "-" + year);
    }

    public String toString() {
        String ans = (this.docName + " " + this.quantity);

        ans += showCalendar(borrowedDay);
        
        ans += showCalendar(deadline);
        
        ans += showCalendar(returnDay);
        
        ans += '\n';

        return ans;
    }
}
