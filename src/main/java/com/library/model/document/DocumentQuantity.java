package com.library.model.document;
public class DocumentQuantity {
    private int total;
    private int current;

    /**
     * Get the number of document including borrowed document.
     * @return total
     */
    public int getTotal() {
        return total;
    }

    /**
     * Get the number of document excluding borrowed document.
     * @return current
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Set the number of document including borrowed document.
     * @param total total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Set the number of document excluding borrowed document.
     * @param current current
     */
    public void setCurrent(int current) {
        this.current = current;
    }
}
