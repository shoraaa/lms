import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users;
    private int totalUsersCount;

    /*
     * List of users whether this element exists.
     */
    private boolean contains(int usersId) {
        if (usersId > this.totalUsersCount) {
            return false;
        }

        return (users.get(usersId) != null);
    }

    /*
     * Import users from JSON file of users data.
     * Create that file if not exist. 
     */
    //public void loadData(String jsonFilePath) {}

    /*
     * Save users data to the desired file path.
     */
    //public void saveData(String jsonFilePath) {}

    /*
     * Create a new user and save it to JSON data.
     * The user's ID will be the total number of users ever created.
     * Return that user's ID.
     */
    public int createUser(UserInfo userInfo) {
        users.add(new User(userInfo, ++this.totalUsersCount));
        return totalUsersCount;
    }

    /*
     * Delete a user based on it ID.
     * Return false if that user not exist.
     */
    public boolean deleteUser(int usersId) {
        if (this.contains(usersId)) {
            users.set(usersId, null);
            return true;
        }

        return false;
    }

    /*
     * Get an array of user's ID which phone number match (even if partialy).
     */
    public ArrayList<Integer> getUserIdsByPhoneNumber(int phoneNumber) {
        ArrayList<Integer> listUsersId = new ArrayList<>();
        for (User aUser : users) {
            if ((aUser != null) && (aUser.matchPhonenumber(phoneNumber))) {
                listUsersId.add(aUser.getUserId());
            }
        }
        return listUsersId;
    }

    /*
     * Get an array of user's ID which name match (even if partialy).
     */
    public ArrayList<Integer> getUserIdsByName(String name) {
        ArrayList<Integer> listUsersId = new ArrayList<>();
        for (User aUser : users) {
            if ((aUser != null) && (aUser.matchUserName(name))) {
                listUsersId.add(aUser.getUserId());
            }
        }
        return listUsersId;
    }

    /*
     * Get an array of all user's ID.
     */
    public ArrayList<Integer> getAllUserIds() {
        ArrayList<Integer> listUsersId = new ArrayList<>();
        for (User aUser : users) {
            if (aUser != null) {
                listUsersId.add(aUser.getUserId());
            }
        }
        return listUsersId;
    }

    /*
     * Get user's info from its id.
     */
    public UserInfo getUserInfoFromId(int userId) {
        if (contains(userId)) {
            return users.get(userId).getInfo();
        }

        return null;
    }

    /*
     * Get user's registered times from its id.
     */
    public LocalDateTime getUserRegisteredTimeFromId(int userId) {
        if (contains(userId)) {
            return users.get(userId).getRegisteredTime();
        }

        return null;
    }
}
