public class UserBorrowAction extends UserAction {
    public UserBorrowAction(int userId, int documentId) {
        super(userId, documentId);
        name = "borrow";
    }
}
