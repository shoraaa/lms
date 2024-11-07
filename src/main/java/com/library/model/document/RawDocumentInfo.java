package com.library.model.document;
import java.time.LocalDate;
import java.util.ArrayList;

public class RawDocumentInfo {
    public String name;
    public ArrayList<Integer> authorIds;
    public ArrayList<Integer> tagIds;
    public String isbn_13;
    public String isbn_10;
    public String publisher;
    public LocalDate publishedDate;

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
}
