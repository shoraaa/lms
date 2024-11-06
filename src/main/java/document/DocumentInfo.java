package base.document;
import java.time.LocalDate;
import java.util.ArrayList;

public class DocumentInfo {
    private String name;
    private ArrayList<Integer> authorIds;
    private ArrayList<Integer> tagIds;
    private String isbn_13;
    private String isbn_10;
    private String publisher;
    private LocalDate publishedDate;

    /**
     * Construct document info from raw document info.
     * @param rawDocumentInfo raw document info
     */
    public DocumentInfo(RawDocumentInfo rawDocumentInfo) {
        name = rawDocumentInfo.getName();
        authorIds = new ArrayList<>(rawDocumentInfo.getAuthorIds());
        tagIds = new ArrayList<>(rawDocumentInfo.getTagIds());
        isbn_13 = rawDocumentInfo.getIsbn_13();
        isbn_10 = rawDocumentInfo.getIsbn_10();
        publisher = rawDocumentInfo.getPublisher();
        publishedDate = rawDocumentInfo.getPublishedDate();
    }

    /**
     * Get name of document.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get author id list.
     * @return authorIds
     */
    public ArrayList<Integer> getAuthorIds() {
        return authorIds;
    }

    /**
     * Get tag id list.
     * @return tagIds
     */
    public ArrayList<Integer> getTagIds() {
        return tagIds;
    }

    /**
     * Get isbn 13.
     * @return isbn 13
     */
    public String getIsbn_13() {
        return isbn_13;
    }

    /**
     * Get isbn 10.
     * @return isbn 10
     */
    public String getIsbn_10() {
        return isbn_10;
    }

    /**
     * Get publisher.
     * @return publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Get published date.
     * @return published date
     */
    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    /**
     * Search an id in author id list
     * @param id id
     * @return true if exist
     */
    public boolean searchAuthor(int id) {
        for (int authorId: authorIds) {
            if (id == authorId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search an id in tag id list
     * @param id id
     * @return true if exist
     */
    public boolean searchTag(int id) {
        for (int tagId: tagIds) {
            if (id == tagId) {
                return true;
            }
        }
        return false;
    }
}
