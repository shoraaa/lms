import java.time.LocalDateTime;

public class User {
    private UserInfo info;
    private LocalDateTime registeredTime;
    private int userId;

    public User(UserInfo info, int userId) {
        this.info = info;
        this.userId = userId;
        this.registeredTime = LocalDateTime.now();
    }

    /** 
     */
    public boolean matchPhonenumber(int phoneNumber) {
        return phoneNumber == info.getPhoneNumber();
    }

    /**
     */
    public boolean matchUserName(String name) {
        return name.equals(info.getName());
    }


    /*
     * Get the user's info.
     */
    public UserInfo getInfo() {
        return info;
    }

    /*
     * Get the user's registered time.
     */
    public LocalDateTime getRegisteredTime() {
        return registeredTime;
    }

    /*
     * Get the user's ID.
     */
    public int getUserId() {
        return userId;
    }
}
