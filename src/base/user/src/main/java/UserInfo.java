package app;

/*
 * UserInfo is a class that contain info of user that are from input (instead of for e.g it's creation date).
 */
class UserInfo {
    private Username name;
    private int phoneNumber; // key

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public UserInfo(Username name, int phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name.getName();
    }
}
