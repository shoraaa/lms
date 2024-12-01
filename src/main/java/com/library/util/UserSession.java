package com.library.util;

import com.library.model.User;

public class UserSession {
    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }
}
