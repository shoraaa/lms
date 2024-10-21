import java.util.ArrayList;

public class UserActionManager {
    private ArrayList<UserAction> actionsLog;

    public UserActionManager() {
        this.actionsLog = new ArrayList<>();
    }

    /*
     * Push an action to the actions list.
     */
    private void push(UserAction action) {
        actionsLog.add(action);
    } 

    /** Do an action of user borrowing a document.
     * @param userId
     * @param documentId
     */
    public void userBorrowDocument(int userId, int documentId) {
        this.push(new UserBorrowAction(userId, documentId));
    }

    /*
     * Do an action of user returning a document.
     */
    public void userReturnDocument(int userId, int documentId) {
        this.push(new UserReturnAction(userId, documentId));
    }

    /*
     * Get an array of user's actions based on the user's ID.
     */
    public ArrayList<UserAction> getUserActionsFromId(int userId) {
        ArrayList<UserAction> aUserActions = new ArrayList<>();
        for (UserAction action : this.actionsLog) {
            if (action.userId == userId) {
                aUserActions.add(action);
            }
        }

        return aUserActions;
    }

    /*
     * Get an array of every user's actions.
     */
    public ArrayList<UserAction> getAllUserActions() {
        return actionsLog;
    }
}
