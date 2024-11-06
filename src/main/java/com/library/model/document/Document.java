package base.document;
import java.time.LocalDateTime;

public class Document {
    private DocumentInfo documentInfo;
    private LocalDateTime registeredTime;
    private int documentId;
    private DocumentQuantity quantity;

    /**
     * Create new document from info and id.
     * @param documentInfo documentInfo
     * @param documentId documentId
     */
    public Document(DocumentInfo documentInfo, int documentId) {
        this.documentInfo = documentInfo;
        this.documentId = documentId;
    }

    /**
     * Get document info.
     * @return document info
     */
    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    /**
     * Get document id.
     * @return document id
     */
    public int getDocumentId() {
        return documentId;
    }

    /**
     * Get the registered time of document.
     * @return registered time
     */
    public LocalDateTime getRegisteredTime() {
        return registeredTime;
    }

    /**
     * Get quantity od document.
     * @return quantity
     */
    public DocumentQuantity getQuantity() {
        return quantity;
    }

    /**
     * Set document quantity.
     * @param quantity quantity
     */
    public void setCurrentQuantity(DocumentQuantity quantity) {
        this.quantity = quantity;
    }

    /*
     * Update document information.
     */
    public void updateDocumentInfo(DocumentInfo newData) {
        this.documentInfo.update(newData);
    }
}
