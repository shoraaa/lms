package app;

public class UserReturnAction extends UserAction {
    public UserReturnAction(int userId, int documentId) {
        super(userId, documentId);
        name = "return";
    }
}
