package library;

/** Thrown when a library rule is broken, for example issuing a book that is already out. */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
