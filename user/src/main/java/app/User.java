package app;

import java.util.ArrayList;
import java.util.Calendar;

public class User {
    protected String UserName;
    protected String RealName;
    protected ArrayList<UserHistory> History = new ArrayList<>();

    public User(String UN, String RN) {
        this.UserName = UN;
        this.RealName = RN;
    }

    public void UpdateBorrowHistory(String DN, int num) {
        UserHistory nH = new UserHistory(DN, num);
        History.add(nH);
    }

    public void UpdateReturnHistory(String DN) {
        for (UserHistory x : History) {
            if ((x.ReturnDay == null) && (x.DocName.equals(DN))) {
                x.ReturnDay = Calendar.getInstance();
                return;
            }
        }
    }

    public String getUserName() {
        return this.UserName;
    }

    public String getRealName() {
        return this.RealName;
    }

    public void getDetail() {
        System.out.println("UserName: " + UserName);
        System.out.println("RealName: " + RealName);
        System.out.println("History:");

        for(UserHistory x: History) {
            x.print();
        }
    }

    public static void main(String[] args) {
        User admin = new User("socnhi", "Ngoc Nhi");
        admin.UpdateBorrowHistory("oop", 4);

        admin.getDetail();
    }

    public ArrayList<UserHistory> getHistory() {
        return History;
    }

    public void setHistory(ArrayList<UserHistory> History) {
        this.History = History;
    }
}
