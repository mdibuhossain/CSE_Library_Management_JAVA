import java.util.Scanner;

class Student {
    Student ID; // this value must be unique, no two different can have same id
    Student name, mobile;

    // add necessary code if need
}

class Book {
    String bookTitle;
    int numOfCopy; // how many copies of this book are in this library

    Book(String bookTitle, int numOfCopy) {

    }

    // this method retures whether this is book available to borrow or not
    boolean isBookAvailable() {
        // implement this method
        return false;
    }

    // when a student borrow this book, then call this method
    void borrow(String student) {
        // implement this method
    }

    // when a student returns this book, then call this method
    void returned(String student) {

    }
}

public class Run {

    // add a new Book in the library
    void addNewBook(Book book) {
        // implement this method

    }

    // search a book in the library whether it is avilable or not
    Book searchBook(String bookTitle) {
        // implement this method
        return null;
    }

    // print all the books in the library
    void printAllBook() {
        // implement this method
    }

    // returns the borrower list of this book
    void printAllBorrower(Book book) {
        // implement this method

    }

    // register a student if he/she is not registered before
    void registration(Student student) {
        // implement this method

    }

    // search the student using studentID
    Student searchStudent(String studentID) {
        // implement this method
        return null;
    }

    // call this method when a student requests to borrow a book
    void borrowRequest(String bookTitle, Student student) {
        // implement this method

    }

    // call this method when a student returns a book
    void returned(String bookTitle, Student student) {
        // implement this method

    }

    public static void main(String[] args) {
        int option;
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println(
                        "Registration: 1 \nAdd New Book: 2 \nPrint Books: 3 \nPrint Borrower: 4 \nBorrow Request: 5 \nReturned: 6");
                System.out.print("Enter Value: ");
                option = scanner.nextInt();
                if (option == 1) {
                    // take input id, name and mobile from student
                    System.out.println("Registration new book");
                } else if (option == 2) {
                    System.out.println("Add new book");
                }
                if (option == 3) {
                    System.out.println("Print Books");
                }
                if (option == 4) {
                    System.out.println("Print Borrower");
                }
                if (option == 5) {
                    System.out.println("Borrow request");
                }
                if (option == 6) {
                    System.out.println("Returned");
                }
            }
        }
    }

}