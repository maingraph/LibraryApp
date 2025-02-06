package library;

public class Book extends LibraryItem implements Searchable, Sortable {
    private String title;
    private String author;
    private int year;

    public Book(String title, String author, int year) {
        super();
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

    @Override
    public String getInfo() {
        return "Book [ID=" + id + ", Title=" + title + ", Author=" + author + ", Year=" + year + "]";
    }

    @Override
    public boolean contains(String keyword) {
        keyword = keyword.toLowerCase();
        return title.toLowerCase().contains(keyword) || author.toLowerCase().contains(keyword);
    }

    @Override
    public int compareTo(LibraryItem other, String criterion) {
        if (!(other instanceof Book)) return 0; // Compare only with other books
        Book o = (Book) other;
        switch (criterion.toLowerCase()) {
            case "title":
                return this.title.compareToIgnoreCase(o.title);
            case "author":
                return this.author.compareToIgnoreCase(o.author);
            case "year":
                return Integer.compare(this.year, o.year);
            default:
                return 0;
        }
    }
}
