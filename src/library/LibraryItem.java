package library;

public abstract class LibraryItem {
    protected int id;
    private static int idCounter = 1; // Automatic ID generation

    public LibraryItem() {
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

    // Method to return basic information about the object
    public abstract String getInfo();
}
