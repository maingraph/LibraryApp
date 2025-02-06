package library;

public interface Sortable {
    /**
     * Compare two LibraryItem objects according to the given criterion.
     * @param other the other LibraryItem to compare to
     * @param criterion the criterion (e.g., "title", "author"/"editor", "year")
     * @return negative, zero, or positive value according to compareTo standard.
     */
    int compareTo(LibraryItem other, String criterion);
}
