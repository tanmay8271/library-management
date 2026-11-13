package library;

/** A person who can borrow books. */
public class Member {
    public static final int MAX_BOOKS = 3;

    private final String id;
    private final String name;
    private int booksOut = 0;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getBooksOut() { return booksOut; }
    public boolean canBorrow() { return booksOut < MAX_BOOKS; }
    void borrowed() { booksOut++; }
    void returned() { booksOut--; }

    @Override
    public String toString() {
        return String.format("%-6s %-24s books out: %d/%d", id, name, booksOut, MAX_BOOKS);
    }
}
