package library;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/** Tiny test runner with no libraries needed. Throws an error if any check fails. */
public class LibraryTest {
    static LocalDate day;

    interface Action { void run() throws Exception; }

    static void check(boolean ok, String name) {
        if (!ok) throw new AssertionError("FAILED: " + name);
        System.out.println("ok  " + name);
    }

    /** The action must be refused with a LibraryException. */
    static void refused(Action a, String name) {
        try {
            a.run();
        } catch (LibraryException e) {
            System.out.println("ok  " + name + "  [" + e.getMessage() + "]");
            return;
        } catch (Exception e) {
            throw new AssertionError("Wrong exception in " + name + ": " + e);
        }
        throw new AssertionError("FAILED (nothing refused): " + name);
    }

    static Library fresh() throws Exception {
        day = LocalDate.of(2026, 1, 1);
        Library l = new Library(() -> day);
        l.addBook("B1", "Clean Code", "Robert Martin");
        l.addBook("B2", "Effective Java", "Joshua Bloch");
        l.addBook("B3", "Head First Java", "Kathy Sierra");
        l.addBook("B4", "Algorithms", "Sedgewick");
        l.addMember("M1", "Asha");
        l.addMember("M2", "Ravi");
        return l;
    }

    public static void main(String[] args) throws Exception {
        // searching
        Library l = fresh();
        check(l.search("JAVA").size() == 2, "search finds by title, ignoring case");
        check(l.search("bloch").size() == 1, "search finds by author");
        check(l.search("zzz").isEmpty(), "search with no match returns nothing");

        // issuing
        l.issue("B1", "M1");
        check(!l.search("clean").get(0).isAvailable(), "issued book is not available");
        final Library a = l;
        refused(() -> a.issue("B1", "M2"), "a book that is out cannot be issued again");
        refused(() -> a.issue("B9", "M1"), "unknown book is refused");
        refused(() -> a.issue("B2", "M9"), "unknown member is refused");

        // borrowing limit
        final Library b = fresh();
        b.issue("B1", "M1");
        b.issue("B2", "M1");
        b.issue("B3", "M1");
        refused(() -> b.issue("B4", "M1"), "a member cannot hold more than 3 books");

        // returning and fines
        final Library c = fresh();
        c.issue("B1", "M1");
        day = day.plusDays(14);
        check(c.giveBack("B1") == 0, "returned on the due date: no fine");
        c.issue("B2", "M1");
        day = day.plusDays(14 + 5);
        check(c.giveBack("B2") == 5 * Loan.FINE_PER_DAY, "5 days late: fine is 5 x Rs 2");
        check(c.search("effective").get(0).isAvailable(), "returned book is available again");
        check(c.allMembers().get(0).getBooksOut() == 0, "member's count goes back to zero");
        refused(() -> c.giveBack("B2"), "returning a book that is not out is refused");

        // input rules
        final Library d = fresh();
        refused(() -> d.addBook("B1", "Dup", "X"), "duplicate book id is refused");
        refused(() -> d.addMember("M1", "Dup"), "duplicate member id is refused");
        refused(() -> d.addBook("B5", "   ", "X"), "blank title is refused");
        refused(() -> d.addBook("B5", "A|B", "X"), "the | character is refused (used by the save file)");

        // saving and loading
        final Library e = fresh();
        e.issue("B3", "M2");
        Path dir = Files.createTempDirectory("lib-test");
        e.save(dir);
        Library back = new Library(() -> day);
        back.load(dir);
        check(back.allBooks().size() == 4 && back.allMembers().size() == 2, "save and load keep books and members");
        check(back.allLoans().size() == 1 && !back.search("head first").get(0).isAvailable(), "save and load keep loans");
        check(back.allMembers().get(1).getBooksOut() == 1, "loaded loan updates the member's count");

        System.out.println("\nAll tests passed.");
    }
}
