package lms.user;

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
    public boolean kmp(String str1, String str2) {
        int[] pi = new int[str2.length()];
        int j = 0;

        for (int i = 0; i < str2.length(); i++) {
            while (j > 0 && (str2.charAt(i) != str1.charAt(j))) {
                j = pi[j - 1];
            }
                
            if (str2.charAt(i) == str1.charAt(j)) {
                j++;
            }

            if (j == str1.length()) {
                return true;
            }
        }

        return false;
    }

    /**
     */
    public boolean matchUserName(String name) {
        return kmp(name, info.getName());
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
