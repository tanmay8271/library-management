package library;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Console menu. Run it, type a number, press Enter. */
public class Main {
    public static void main(String[] args) throws Exception {
        Path data = Path.of("data");
        Library lib = new Library();
        lib.load(data);
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("""

                === Library Management System ===
                1. Add book          5. Return book
                2. Add member        6. List all books
                3. Search books      7. List members
                4. Issue book        8. List issued books
                0. Save and exit""");
            System.out.print("Choose: ");
            if (!in.hasNextLine()) break;
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        Book b = lib.addBook(ask(in, "Book id"), ask(in, "Title"), ask(in, "Author"));
                        System.out.println("Added: " + b.getTitle());
                    }
                    case "2" -> {
                        Member m = lib.addMember(ask(in, "Member id"), ask(in, "Name"));
                        System.out.println("Added member: " + m.getName());
                    }
                    case "3" -> show(lib.search(ask(in, "Search title or author")), "No matching books.");
                    case "4" -> {
                        Loan l = lib.issue(ask(in, "Book id"), ask(in, "Member id"));
                        System.out.println("Issued. Due on " + l.getDue());
                    }
                    case "5" -> {
                        int fine = lib.giveBack(ask(in, "Book id"));
                        System.out.println(fine == 0 ? "Returned. No fine." : "Returned late. Fine: Rs " + fine);
                    }
                    case "6" -> show(lib.allBooks(), "No books yet.");
                    case "7" -> show(lib.allMembers(), "No members yet.");
                    case "8" -> show(lib.allLoans(), "Nothing is issued.");
                    case "0" -> {
                        lib.save(data);
                        System.out.println("Saved. Goodbye.");
                        return;
                    }
                    default -> System.out.println("Please choose 0 to 8.");
                }
            } catch (LibraryException e) {
                System.out.println("Cannot do that: " + e.getMessage());
            }
        }
        lib.save(data); // input ended (for example Ctrl+Z): still save
    }

    private static String ask(Scanner in, String label) {
        System.out.print(label + ": ");
        return in.hasNextLine() ? in.nextLine() : "";
    }

    private static void show(List<?> items, String empty) {
        if (items.isEmpty()) System.out.println(empty);
        else items.forEach(System.out::println);
    }
}
