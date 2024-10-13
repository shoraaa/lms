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

    public User getUser() {
        Scanner input = new Scanner(System.in);
        String userName = new String();

        while(true) { 
            System.out.print("Enter userName: ");
            userName = input.nextLine();
            userName = userName.trim();
            if (contains(userName)) {
                throw new ArithmeticException("This userName already exists!Please redo!");
            } else break;
        }

        System.out.print("Enter realName: ");
        String realName = input.nextLine();
        realName = realName.trim();
        input.close();
        
        return User.creUser(userName, realName);
    }

    public void addNewUser() {
        userList.add(getUser());
    }

    public String getUserDetail(String userName) {
        if (contains(userName)) {
            User realName = getUser(userName);
            return realName.getDetail();

        } else throw new ArithmeticException("This userName does not exists!");
    }
}
