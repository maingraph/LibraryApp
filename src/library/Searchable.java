package library;

public interface Searchable {
    // Returns true if the object contains the keyword (e.g. in title or author/editor)
    boolean contains(String keyword);
}
