package app;

import java.util.Calendar;

public class User {
    protected String userName;
    protected String realName;
    protected Calendar accCreDate;
    protected Histories histories = new Histories();

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

    public void updateBorrowHistory(String docName, int num) {
        UserHistory newHistories = new UserHistory(docName, num);
        histories.add(newHistories);
    }

    public void updateReturnHistory(String docName) {
        histories.updateReturnDay(docName);
    }

    public String getUserName() {
        return this.userName;
    }

    public String getRealName() {
        return this.realName;
    }

    public String getDetail() {
        String ans = ("userName: " + userName + '\n');
        ans += ("realName: " + realName + '\n');
        ans += "AccouserNamet creation date: " + UserHistory.showCalendar(accCreDate);
        ans += '\n';

        ans += "histories:\n"  + histories.toString();
        return ans;
    }

    public Histories getHistories() {
        return histories;
    }

    public void setHistories(Histories histories) {
        this.histories = histories;
    }
}
