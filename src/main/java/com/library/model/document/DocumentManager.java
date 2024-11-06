package base.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

public class DocumentManager {
    private TreeMap<Integer, Document> documents;
    private ArrayList<String> authorsList;
    private ArrayList<String> tagsList;
    private int totalDocumentsCount;

    /**
     * Constuctor of DocumentManager.
     */
    public DocumentManager() {
        documents = new TreeMap<>();
        authorsList = new ArrayList<>();
        tagsList = new ArrayList<>();
    }

    /*
     * Import users from JSON file of documents data.
     * Create that file if not exist.
     */
    public void loadData(String jsonFilePath) {
    }

    /*
     * Save documents data to the desired file path.
     */
    public void saveData(String jsonFilePath) {
    }

    /*
     *  Check if text contain pattern.
     */
    private boolean search(String pattern, String text) {
        String s = pattern + text;
        int[] lps = new int[s.length()];
        for (int i = 0; i < lps.length; i++) {
            lps[i] = 0;
        }
        for (int i = 1; i < s.length(); i++) {
            lps[i] = lps[i-1];
            while (lps[i] > 0 && s.charAt(lps[i]+1) == s.charAt(i)) {
                lps[i] = lps[lps[i]];
            }
            if (lps[i] >= pattern.length()) {
                return true;
            }
        }
        return false;
    }

    /*
     * Get author name based on id.
     * Create new if not exist.
     */
    public String getAuthorName(int authorId) {
        return authorsList.get(authorId);
    }

    /*
     * Get an array of author id that name matched (even if partialy).
     */
    public ArrayList<Integer> getAuthorIdsByName(String author) {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (int i = 0; i < authorsList.size(); i++) {
            if (search(author, authorsList.get(i))) {
                returnList.add(i);
            }
        }
        return returnList;
    }

    /*
     * Get an array all author id.
     */
    public ArrayList<Integer> getAllAuthorIds() {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (int i = 1; i <= authorsList.size(); i++) {
            returnList.add(i);
        }
        return returnList;
    }

    /*
     * Get tag name based on id.
     * Create new if not exist.
     */
    public String getTagName(int tagId) {
        return tagsList.get(tagId);
    }

    /*
     * Get an array of tag id that name matched (even if partialy).
     */
    public ArrayList<Integer> getTagIdsByName(String tag) {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (int i = 0; i < tagsList.size(); i++) {
            if (search(tag, tagsList.get(i))) {
                returnList.add(i);
            }
        }
        return returnList;
    }

    /*
     * Get an array all tag id.
     */
    public ArrayList<Integer> getAllTagIds() {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (int i = 1; i <= tagsList.size(); i++) {
            returnList.add(i);
        }
        return returnList;
    }

    /*
     * Register a new document and save it to JSON data.
     * The document's ID will be the total number of documents ever registered.
     * The authors and tags not exist will be created and registered into its database.
     * Return that document's ID.
     */
    public int registerDocument(RawDocumentInfo rawDocumentInfo) {
        DocumentInfo documentInfo = new DocumentInfo(rawDocumentInfo);
        Document document = new Document(documentInfo, ++totalDocumentsCount);
        documents.put(totalDocumentsCount, document);
        return totalDocumentsCount;
    }

    /*
     * Delete a document based on it ID.
     * Return false if that document not exist.
     */
    public boolean deleteDocument(int documentId) {
        if (!documents.containsKey(documentId)) {
            return false;
        } else {
            documents.remove(documentId);
            return true;
        }
    }

    /*
     * Get an array of document's ID which name match (even if partialy).
     */
    public ArrayList<Integer> getDocumentIdsByName(String name) {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (Document document: documents.values()) {
            if (search(name, document.getDocumentInfo().getName())) {
                returnList.add(document.getDocumentId());
            }
        }
        return returnList;
    }

    /*
     * Get an array of document's ID which author match one of the authors.
     */
    public ArrayList<Integer> getDocumentIdsByAuthorId(int authorId) {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (Document document: documents.values()) {
            if (document.getDocumentInfo().searchAuthor(authorId)) {
                returnList.add(document.getDocumentId());
            }
        }
        return returnList;
    }

    /*
     * Get an array of document's ID which tag match one of the tags.
     */
    public ArrayList<Integer> getDocumentIdsByTagId(int tagId) {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (Document document: documents.values()) {
            if (document.getDocumentInfo().searchTag(tagId)) {
                returnList.add(document.getDocumentId());
            }
        }
        return returnList;
    }

    /*
     * Get an array of all document's ID.
     */
    public ArrayList<Integer> getAllDocumentIds() {
        ArrayList<Integer> returnList = new ArrayList<>();
        for (Document document: documents.values()) {
            returnList.add(document.getDocumentId());
        }
        return returnList;
    }

    /*
     * Get document's info from its id.
     */
    public DocumentInfo getDocumentInfoFromId(int documentId) {
        return documents.get(documentId).getDocumentInfo();
    }

    /*
     * Get document's info from its id.
     */
    public Document getDocumentFromId(int documentId) {
        return documents.get(documentId);
    }

    /*
     * Get document's quantity from its id.
     */
    public DocumentQuantity getDocumentQuantityFromId(int documentId) {
        return documents.get(documentId).getQuantity();
    }

    /*
     * Get document's registered time from its id.
     */
    public LocalDateTime getDocumentRegisteredTimeFromId(int documentId) {
        return documents.get(documentId).getRegisteredTime();
    }

    /*
     * Borrow a document by it id.
     * Return false if quantity = 0.
     */
    public boolean borrowDocument(int documentId) {
        int currentQuantity = documents.get(documentId).getQuantity().getCurrent();
        if (currentQuantity == 0) {
            return false;
        } else {
            documents.get(documentId).getQuantity().setCurrent(currentQuantity - 1);
            return true;
        }
    }

    /*
     * Return a document by it id.
     */
    public void returnDocument(int documentId) {
        int currentQuantity = documents.get(documentId).getQuantity().getCurrent();
        if (currentQuantity == documents.get(documentId).getQuantity().getTotal()) {
            throw new IllegalStateException("Document has not been borrowed yet.");
        }
        documents.get(documentId).getQuantity().setCurrent(currentQuantity + 1);
    }

}
