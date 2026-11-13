package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Records that one member has one book, and when it is due. */
public class Loan {
    public static final int LOAN_DAYS = 14;
    public static final int FINE_PER_DAY = 2; // rupees

    private final Book book;
    private final Member member;
    private final LocalDate issued;
    private final LocalDate due;

    public Loan(Book book, Member member, LocalDate issued) {
        this.book = book;
        this.member = member;
        this.issued = issued;
        this.due = issued.plusDays(LOAN_DAYS);
    }

    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getIssued() { return issued; }
    public LocalDate getDue() { return due; }

    /** Fine in rupees if returned on the given day. Zero when on time. */
    public int fineOn(LocalDate day) {
        long late = ChronoUnit.DAYS.between(due, day);
        return late > 0 ? (int) late * FINE_PER_DAY : 0;
    }

    @Override
    public String toString() {
        return String.format("%-6s %-28s -> %-16s due %s", book.getId(), book.getTitle(), member.getName(), due);
    }
}
