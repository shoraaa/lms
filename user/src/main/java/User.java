import java.util.ArrayList;

public class User {
    protected String UserName;
    protected String RealName;
    protected ArrayList<UserHistory> History;

    public User(String UN, String RN) {
        this.UserName = UN;
        this.RealName = RN;
    }

    public void UpdateBorrowHistory(String DN, int num, Date br) {
        UserHistory nH = new UserHistory(DN, num, br);
        History.add(nH);
    }

    public void UpdateReturnHistory(String DN, Date re) {
        for (UserHistory x : History) {
            if ((x.ReturnDay == null) && (x.DocName.equals(DN))) {
                x.ReturnDay = re;
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
}
