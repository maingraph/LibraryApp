package library;

public class Magazine extends LibraryItem implements Searchable, Sortable {
    private String title;
    private String editor;
    private int year;
    private int issueNumber;

    public Magazine(String title, String editor, int year, int issueNumber) {
        super();
        this.title = title;
        this.editor = editor;
        this.year = year;
        this.issueNumber = issueNumber;
    }

    public String getTitle() { return title; }
    public String getEditor() { return editor; }
    public int getYear() { return year; }
    public int getIssueNumber() { return issueNumber; }

    @Override
    public String getInfo() {
        return "Magazine [ID=" + id + ", Title=" + title + ", Editor=" + editor +
                ", Year=" + year + ", Issue=" + issueNumber + "]";
    }

    @Override
    public boolean contains(String keyword) {
        keyword = keyword.toLowerCase();
        return title.toLowerCase().contains(keyword) || editor.toLowerCase().contains(keyword);
    }

    @Override
    public int compareTo(LibraryItem other, String criterion) {
        if (!(other instanceof Magazine)) return 0; // Compare only with other magazines
        Magazine o = (Magazine) other;
        switch (criterion.toLowerCase()) {
            case "title":
                return this.title.compareToIgnoreCase(o.title);
            case "editor":
                return this.editor.compareToIgnoreCase(o.editor);
            case "year":
                return Integer.compare(this.year, o.year);
            default:
                return 0;
        }
    }
}
