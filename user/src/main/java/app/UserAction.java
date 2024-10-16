package app;

import java.time.LocalDateTime;

public class UserAction {
    int userId;
    int documentId;
    LocalDateTime time;
    String name;

    public UserAction(){}
    
    public UserAction(int userId, int documentId) {
        this.userId = userId;
        this.documentId = documentId;
        this.time = LocalDateTime.now();
    }
}
