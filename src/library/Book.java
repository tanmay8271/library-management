package library;

/** One book title kept by the library. Each book has a unique id. */
public class Book {
    private final String id;
    private final String title;
    private final String author;
    private boolean available = true;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    void setAvailable(boolean available) { this.available = available; }

    /** True if the text appears in the title or the author, ignoring case. */
    public boolean matches(String text) {
        String t = text.toLowerCase();
        return title.toLowerCase().contains(t) || author.toLowerCase().contains(t);
    }

    @Override
    public String toString() {
        return String.format("%-6s %-32s %-22s %s", id, title, author, available ? "Available" : "Issued");
    }
}
