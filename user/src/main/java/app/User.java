package app;

import java.util.ArrayList;
import java.util.Calendar;

public class User {
    protected String userName;
    protected String realName;
    protected Calendar accCreDate;
    protected ArrayList<UserHistory> histories = new ArrayList<>();

    public User() {}

    public User(String userName, String realName) {
        this.userName = userName;
        this.realName = realName;
        this.accCreDate = Calendar.getInstance();
    }

    public static User creUser(String userName, String realName) {
        User result = new User(userName, realName);
        return result;
    }

    public void UpdateBorrowHistory(String docName, int num) {
        UserHistory newHistories = new UserHistory(docName, num);
        histories.add(newHistories);
    }

    public void UpdateReturnHistory(String docName) {
        for (UserHistory x : histories) {
            if ((x.returnDay == null) && (x.docName.equals(docName))) {
                x.returnDay = Calendar.getInstance();
                return;
            }
        }
    }

    public String getUserName() {
        return this.userName;
    }

    public String getRealName() {
        return this.realName;
    }

    public void getDetail() {
        System.out.println("userName: " + userName);
        System.out.println("realName: " + realName);
        System.out.print("AccouserNamet creation date: ");
        UserHistory.showCalendar(accCreDate);
        System.out.println();

        System.out.println("histories:");

        for(UserHistory x: histories) {
            x.print();
        }
    }

    public ArrayList<UserHistory> getHistories() {
        return histories;
    }

    public void setHistories(ArrayList<UserHistory> histories) {
        this.histories = histories;
    }
}
