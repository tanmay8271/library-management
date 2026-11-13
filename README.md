# Library Management System

A console program written in Java to practise object-oriented design. It keeps books and members, issues and returns books, and charges a fine for late returns. Data is saved in text files between runs.

**Stack:** Java 17 or newer (no libraries, no build tool).

## What it does
- Add books and members.
- Search books by title or author (any case).
- Issue a book for 14 days. A member can hold at most 3 books at once.
- Return a book. Late returns cost Rs 2 per day.
- List all books, all members and all issued books.
- Saves to a `data` folder on exit and loads it on start.

## Run it on Windows (VS Code terminal)
Check Java first: `java -version` should say 17 or higher (install a JDK from adoptium.net if not).

Easiest, from the project folder:
```
.\run.bat
```
Or by hand:
```
javac -d out src\library\*.java
java -cp out library.Main
```
Type a number from the menu and press Enter. Choose 0 to save and exit.

## Tests
```
.\test.bat
```
20 checks run: search, issue rules, the 3-book limit, fines, input rules and save/load. They use a fixed date, so late-return fines are tested without waiting.

## How the code is organised (OOP)
| Class | Job |
|---|---|
| `Book` | One title: id, title, author, available or not |
| `Member` | One borrower and how many books they hold |
| `Loan` | Links a book to a member, with the due date and the fine calculation |
| `Library` | All the rules and the save/load code. Knows nothing about the menu |
| `LibraryException` | A custom error for broken rules, shown to the user as a message |
| `Main` | The text menu only |

Ideas used: encapsulation (private fields, package-private setters so only `Library` changes availability), a custom checked exception, a `Supplier<LocalDate>` passed in so tests can control "today", and java.nio.file for saving.

## Ideas to extend it
Reserve a book, per-category search, a member history, or replace the text files with a database.
