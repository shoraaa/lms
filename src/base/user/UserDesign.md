```java
class Username {
    private String lastName;
    private String middleName;
    private String firstName;
}
```
```java
/*
 * UserInfo is a class that contain info of user that are from input (instead of for e.g it's creation date).
 */
class UserInfo {
    private Username name;
    private int phoneNumber; // key
}
```
```java
class User {
    private UserInfo info;
    private LocalDateTime registeredTime;
    private int userId;

    User(UserInfo info, int userId) {}


    /*
     * Get the user's info.
     */
    UserInfo getInfo() {}

    /*
     * Get the user's registered time.
     */
    LocalDateTime getRegisteredTime() {};
}
```
```java
class UserManager {
    private ArrayList<User> users;
    private int totalUsersCount;

    /*
     * Import users from JSON file of users data.
     * Create that file if not exist. 
     */
    public void loadData(String jsonFilePath) {}

    /*
     * Save users data to the desired file path.
     */
    public void saveData(String jsonFilePath) {}

    /*
     * Create a new user and save it to JSON data.
     * The user's ID will be the total number of users ever created.
     * Return that user's ID.
     */
    public int createUser(UserInfo userInfo) {}

    /*
     * Delete a user based on it ID.
     * Return false if that user not exist.
     */
    public bool deleteUser(int usersId) {}

    /*
     * Get an array of user's ID which phone number match (even if partialy).
     */
    public ArrayList<Integer> getUserIdsByPhoneNumber(int phoneNumber) {}

    /*
     * Get an array of user's ID which name match (even if partialy).
     */
    public ArrayList<Integer> getUserIdsByName(String name) {}

    /*
     * Get an array of all user's ID.
     */
    public ArrayList<Integer> getAllUserIds() {}

    /*
     * Get user's info from its id.
     */
    public UserInfo getUserInfoFromId(int userId) {}

    /*
     * Get user's registered times from its id.
     */
    public LocalDateTime getUserRegisteredTimeFromId(int userId) {}

}
```
```java
class UserAction {
    int userId;
    int documentId;
    LocalDateTime time;
    String name;

    UserAction(int userId, int documentId) {}
}
```
```java
class UserBorrowAction extends UserAction {

    UserBorrowAction(int userId, int documentId) {
        super(userId, documentId);
        name = "borrow";
    }
}
```
```java
class UserReturnAction extends UserAction {
    
    UserReturnAction(int userId, int documentId) {
        super(userId, documentId);
        name = "return";
    }
}
```
```java
class UserActionManager {
    private ArrayList<UserAction> actionsLog;

    /*
     * Push an action to the actions list.
     */
    private push(UserAction action) {} 

    /*
     * Do an action of user borrowing a document.
     */
    public userBorrowDocument(int userId, int documentId) {}

    /*
     * Do an action of user returning a document.
     */
    public userReturnDocument(int userId, int documentId) {}

    /*
     * Get an array of user's actions based on the user's ID.
     */
    public ArrayList<UserAction> getUserActionsFromId(int userId) {}

    /*
     * Get an array of every user's actions.
     */
    public ArrayList<UserAction> getAllUserActions() {}

}
```