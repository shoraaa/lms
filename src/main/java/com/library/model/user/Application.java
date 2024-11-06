package com.library.model.user;
// import base.document.*;


public class Application {
    private UserActionManager action;
    private UserManager userList;
    //private DocumentManager documentList;

    /** [6] Add User
     * 
     */
    public void addUser(UserInfo info) {
        userList.createUser(info);
    }

    /** [7] Borrow Document
     * 
     */
    public boolean borrowDocumentByID(int documentId, int userId) {
        if (!userList.contains(userId)) {
            return false;
        }
        /*
        if (!documentList.contains(documentId)
            || !documentList.borrowDocument(documentId) {
            return false;
        }
         */

        action.userBorrowDocument(userId, documentId);

        return true;
    }

    /** [8] Return Document
     * 
     */
    public boolean returnDocumentByID(int documentId, int userId) {
        if (!userList.contains(userId)) {
            return false;
        }
        /*
        if (!documentList.contains(documentId) {
            return false;
        }
         */

        action.userReturnDocument(userId, documentId);
        //documentList.returnDocument(documentId);

        return true;
    }

    /** [9] Display User Info
     * 
     */
    public UserInfo displayUserInfo(int userId) {
        return userList.getUserInfoFromId(userId);
    }

    public void menu(int operation) {
        switch (operation) {
            case 6:
                //addUser(info);
                break;
            case 7:
                //borrowDocumentByID(documentId, userId);
                break;
            case 8:
                //returnDocumentByID(documentId, userId)
                break;
            case 9:
                //displayUserInfo(userId)
                break;
            default:
                throw new AssertionError();
        }
    }
}
