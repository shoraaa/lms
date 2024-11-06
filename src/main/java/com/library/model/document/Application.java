package base.document;

public class Application {
    private DocumentManager documentManager;

    /**
     * [0] Exit.
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * [1] Add document.
     * @param rawDocumentInfo raw document info
     */
    public int addDocument(RawDocumentInfo newData) {
        return documentManager.registerDocument(newData);
    }

    /**
     * [2] Remove document.
     * @param documentId document id
     * @return true if can remove
     */
    public boolean removeDocument(int documentId) {
        return documentManager.deleteDocument(documentId);
    }

    /**
     * [3] Update document's iunfromation.
     * @param documentId document id
     * @param newData new information
     */
    public void updateDocumentInfo(int documentId, RawDocumentInfo newData) {
        DocumentInfo documentInfo = new DocumentInfo(newData);
        documentManager.getDocumentInfoFromId(documentId).update(documentInfo);
    }

    /**
     * [4] Find document by it id.
     * @param documentId document id
     * @return document
     */
    public Document findDocument(int documentId) {
        return documentManager.getDocumentFromId(documentId);
    }

    /**
     * [5] Display document.
     */
    public void displayDocument() {
        //documentManager.displayDocumentInfo;
    }
}
