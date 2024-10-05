package app;
import java.util.ArrayList;
import java.util.Scanner;

public class UserManager {
    protected ArrayList<User> UserList = new ArrayList<>();

    public int getIndex(String UserName) {
        for (int i = 0; i < UserList.size(); i++) {
            if (UserList.get(i).getUserName().equals(UserName)) {
                return i;
            }
        }

        return -1;
    }

    public boolean contains(String UserName) {
        return (getIndex(UserName) != -1);
    }

    public User getUser(String UserName) {
        return UserList.get( getIndex(UserName) );
    }

    public void addNewUser() {
        Scanner input = new Scanner(System.in);
        String ansUN = new String();

        while(true) { 
            System.out.print("Enter UserName: ");
            ansUN = input.nextLine();
            ansUN = ansUN.trim();
            if (contains(ansUN)) {
                System.out.println("This UserName already exists!Please redo!");
            } else break;
        } 
        
        System.out.print("Enter RealName: ");
        String ansRN = input.nextLine();
        ansRN = ansRN.trim();

        UserList.add(User.creUser(ansUN, ansRN));
    }

    public void getUserDetail(String UserName) {
        if (contains(UserName)) {
            User ans = getUser(UserName);
            ans.getDetail();

        } else System.out.println("This UserName does not exists!");
    }
}
