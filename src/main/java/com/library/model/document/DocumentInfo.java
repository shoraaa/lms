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

    /*
     * Set name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * Set author id.
     */
    public void setAuthorIds(ArrayList<Integer> authorIds) {
        this.authorIds = authorIds;
    }

    /*
    * Set tag id.
    */
    public void setTagIds(ArrayList<Integer> tagIds) {
        this.tagIds = tagIds;
    }
    
    /*
     * Set isbn_13.
     */
    public void setIsbn_13(String isbn_13) {
        this.isbn_13 = isbn_13;
    }

    /*
     * Set isbn 10.
     */
    public void setIsbn_10(String isbn_10) {
        this.isbn_10 = isbn_10;
    }

    /*
     * Set publisher.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /*
     * Set publisher date.
     */
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
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

    /**
     * Update document information.
     * @param newData new document information
     */    
    public void update(DocumentInfo newData) {
        setName(newData.name);
        setAuthorIds(newData.authorIds);
        setTagIds(newData.tagIds);
        setIsbn_10(newData.isbn_10);
        setIsbn_13(newData.isbn_13);
        setPublishedDate(newData.publishedDate);
        setPublisher(newData.publisher);
    }
    
}
