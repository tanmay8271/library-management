package library;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** The rules of the library: books, members, issuing, returning, and saving to files. */
public class Library {
    private final Map<String, Book> books = new LinkedHashMap<>();
    private final Map<String, Member> members = new LinkedHashMap<>();
    private final Map<String, Loan> loans = new LinkedHashMap<>(); // key = book id
    private final Supplier<LocalDate> today; // a supplier so tests can control the date

    public Library() {
        this(LocalDate::now);
    }

    public Library(Supplier<LocalDate> today) {
        this.today = today;
    }

    public Book addBook(String id, String title, String author) throws LibraryException {
        id = clean(id, "Book id");
        title = clean(title, "Title");
        author = clean(author, "Author");
        if (books.containsKey(id)) throw new LibraryException("A book with id " + id + " already exists");
        Book b = new Book(id, title, author);
        books.put(id, b);
        return b;
    }

    public Member addMember(String id, String name) throws LibraryException {
        id = clean(id, "Member id");
        name = clean(name, "Name");
        if (members.containsKey(id)) throw new LibraryException("A member with id " + id + " already exists");
        Member m = new Member(id, name);
        members.put(id, m);
        return m;
    }

    public List<Book> search(String text) {
        List<Book> found = new ArrayList<>();
        for (Book b : books.values()) if (b.matches(text.trim())) found.add(b);
        return found;
    }

    public Loan issue(String bookId, String memberId) throws LibraryException {
        Book book = books.get(bookId);
        Member member = members.get(memberId);
        if (book == null) throw new LibraryException("No book with id " + bookId);
        if (member == null) throw new LibraryException("No member with id " + memberId);
        if (!book.isAvailable()) throw new LibraryException("\"" + book.getTitle() + "\" is already issued");
        if (!member.canBorrow()) throw new LibraryException(member.getName() + " already has " + Member.MAX_BOOKS + " books");
        Loan loan = new Loan(book, member, today.get());
        book.setAvailable(false);
        member.borrowed();
        loans.put(bookId, loan);
        return loan;
    }

    /** Returns the fine in rupees (0 if on time). */
    public int giveBack(String bookId) throws LibraryException {
        Loan loan = loans.get(bookId);
        if (loan == null) throw new LibraryException("Book " + bookId + " is not issued");
        loans.remove(bookId);
        loan.getBook().setAvailable(true);
        loan.getMember().returned();
        return loan.fineOn(today.get());
    }

    public List<Book> allBooks() { return new ArrayList<>(books.values()); }
    public List<Member> allMembers() { return new ArrayList<>(members.values()); }
    public List<Loan> allLoans() { return new ArrayList<>(loans.values()); }

    private static String clean(String value, String label) throws LibraryException {
        if (value == null || value.isBlank()) throw new LibraryException(label + " cannot be empty");
        String v = value.trim();
        if (v.contains("|")) throw new LibraryException(label + " cannot contain the | character");
        return v;
    }

    // ---- saving and loading: plain text, one record per line, fields split by | ----

    public void save(Path folder) throws IOException {
        Files.createDirectories(folder);
        List<String> b = new ArrayList<>(), m = new ArrayList<>(), l = new ArrayList<>();
        for (Book x : books.values()) b.add(x.getId() + "|" + x.getTitle() + "|" + x.getAuthor());
        for (Member x : members.values()) m.add(x.getId() + "|" + x.getName());
        for (Loan x : loans.values()) l.add(x.getBook().getId() + "|" + x.getMember().getId() + "|" + x.getIssued());
        Files.write(folder.resolve("books.txt"), b, StandardCharsets.UTF_8);
        Files.write(folder.resolve("members.txt"), m, StandardCharsets.UTF_8);
        Files.write(folder.resolve("loans.txt"), l, StandardCharsets.UTF_8);
    }

    public void load(Path folder) throws IOException, LibraryException {
        if (!Files.exists(folder.resolve("books.txt"))) return; // first run: nothing saved yet
        for (String line : Files.readAllLines(folder.resolve("books.txt"), StandardCharsets.UTF_8)) {
            String[] p = line.split("\\|", -1);
            if (p.length == 3) addBook(p[0], p[1], p[2]);
        }
        for (String line : Files.readAllLines(folder.resolve("members.txt"), StandardCharsets.UTF_8)) {
            String[] p = line.split("\\|", -1);
            if (p.length == 2) addMember(p[0], p[1]);
        }
        for (String line : Files.readAllLines(folder.resolve("loans.txt"), StandardCharsets.UTF_8)) {
            String[] p = line.split("\\|", -1);
            if (p.length != 3) continue;
            Book book = books.get(p[0]);
            Member member = members.get(p[1]);
            if (book == null || member == null) continue;
            Loan loan = new Loan(book, member, LocalDate.parse(p[2]));
            book.setAvailable(false);
            member.borrowed();
            loans.put(p[0], loan);
        }
    }
}
