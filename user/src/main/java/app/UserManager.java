package app;
import java.util.ArrayList;
import java.util.Scanner;

public class UserManager {
    protected ArrayList<User> userList = new ArrayList<>();

    public int getIndex(String userName) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserName().equals(userName)) {
                return i;
            }
        }

        return -1;
    }

    public boolean contains(String userName) {
        return (getIndex(userName) != -1);
    }

    public User getUser(String userName) {
        return userList.get( getIndex(userName) );
    }

    public String getUserName() {
        Scanner input = new Scanner(System.in);
        String ans = new String();

        while(true) { 
            System.out.print("Enter userName: ");
            ans = input.nextLine();
            ans = ans.trim();
            if (contains(ans)) {
                System.out.println("This userName already exists!Please redo!");
            } else break;
        }
        input.close();

        return ans;
    }

    public String getRealName() {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter realName: ");
        String ans = input.nextLine();
        ans = ans.trim();
        input.close();

        return ans;
    }

    public void addNewUser() {
        userList.add(User.creUser(getUserName(), getRealName()));
    }

    public void getUserDetail(String userName) {
        if (contains(userName)) {
            User ans = getUser(userName);
            ans.getDetail();

        } else System.out.println("This userName does not exists!");
    }
}
