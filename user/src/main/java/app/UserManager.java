package app;
import java.util.ArrayList;
import java.util.Scanner;

public class UserManager {
    protected ArrayList<User> UserList = new ArrayList<>();

    public boolean contains(String UserName) {
        return (UserList.indexOf(UserName) != -1);
    }

    public void addNewUser() {
        Scanner input = new Scanner(System.in);
        String ansUN = new String();

        while(true) { 
            System.out.print("Enter UserName: ");
            ansUN = input.nextLine();
            ansUN = ansUN.trim();
            if (contains(ansUN)) {
                System.out.println("This UserName already exists!");
            } else break;
        } 
        
        System.out.print("Enter RealName: ");
        String ansRN = input.nextLine();
        ansRN = ansRN.trim();

        UserList.add(User.creUser(ansUN, ansRN));
    }
}
