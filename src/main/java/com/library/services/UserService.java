package com.library.services;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.library.model.User;

public class UserService {

    public void addUsersFromJson(String jsonFilePath) {
        Gson gson = new Gson();
        UserDAO userDAO = new UserDAO();
        
        try (FileReader reader = new FileReader(jsonFilePath)) {
            // Define the type of list for Gson to deserialize
            Type userListType = new TypeToken<List<User>>() {}.getType();
            
            // Parse JSON file to List of Users
            List<User> users = gson.fromJson(reader, userListType);

            for (User user : users) {
                // Directly add each user to the database
                userDAO.add(user);
            }
            
            System.out.println("Users added successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to add users from JSON file.");
        }
    }
}
