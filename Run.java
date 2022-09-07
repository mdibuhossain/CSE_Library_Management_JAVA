import java.beans.Expression;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.catalog.Catalog;

class Print {
    void println(String st) {
        System.out.println(st);
    }

    void print(String st) {
        System.out.print(st);
    }
}

class FileIO {
    private HashMap<String, String> Dir = new HashMap<String, String>();

    public FileIO() {
        Dir.put("studentsPath", "data\\students.bin");
        Dir.put("booksPath", "data\\books.bin");
    }

    public void writeObjectToFile(Object obj, String path) throws IOException {
        File file = new File(Dir.get(path));
        long fileSize = file.length();
        FileOutputStream fileOut = new FileOutputStream(Dir.get(path), true);
        ObjectOutputStream objectOut = null;
        if (fileSize == 0) {
            objectOut = new ObjectOutputStream(fileOut);
        } else {
            objectOut = new AppendingObjectOutputStream(fileOut);
        }
        try {
            System.out.println(fileSize);
            objectOut.writeObject(obj);
            objectOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectOut.close();
            fileOut.close();
        }
    }

    public void printObjectFromFile(Object[] objs, String path) throws IOException {
        FileInputStream fileIn = new FileInputStream(Dir.get(path));
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        try {
            while (fileIn.available() != 0) {
                Object tmp = (Object) objectIn.readObject();
                // tmp.getAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileIn.close();
            objectIn.close();
        }
    }
}

class Student {
    public String name;
    public String id;
    public String phone;

    public Student() {
        name = id = phone = "";
    }
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

public class Run extends FileIO {

    // add a new Book in the library
    void addNewBook(Book book) {
        // implement this method
        System.out.println("Add new book");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    // search a book in the library whether it is avilable or not
    Book searchBook(String bookTitle) {
        // implement this method
        return null;
    }

    // print all the books in the library
    void printAllBook() {
        // implement this method
        System.out.println("Print Books");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    // returns the borrower list of this book
    void printAllBorrower(Book book) {
        // implement this method
        System.out.println("Print Borrower");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    // register a student if he/she is not registered before
    void registration() throws IOException {
        // Print p = new Print();
        // String fmt = "%1$4s %2$10s %3$10s%n";
        Scanner sc = new Scanner(System.in);
        Student student = new Student();
        System.out.println("Registration new book\n");
        System.out.printf("%-20s", "Name");
        while (student.name.length() == 0) {
            student.name = sc.nextLine();
        }

        System.out.printf("%-20s", "ID");
        while (student.id.length() == 0) {
            student.id = sc.nextLine();
        }

        System.out.printf("%-20s", "Phone");
        while (student.phone.length() == 0) {
            student.phone = sc.nextLine();
        }
        writeObjectToFile(student, "stduentsPath");
    }

    // search the student using studentID
    Student searchStudent(String studentID) {
        // implement this method
        return null;
    }

    // call this method when a student requests to borrow a book
    void borrowRequest(String bookTitle, Student student) {
        // implement this method
        Print p = new Print();
        p.println("Borrow request");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    // call this method when a student returns a book
    void returned(String bookTitle, Student student) {
        // implement this method
        Print p = new Print();
        p.println("Returned");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            File dataPath = new File("data");
            dataPath.mkdir();
            int option;
            Run tmp = new Run();
            Student student = new Student();
            Book book = null;
            Print p = new Print();
            while (true) {
                p.print("\033[H\033[2J");
                System.out.flush();
                p.println(
                        "Registration: 1 \nAdd New Book: 2 \nPrint Books: 3 \nPrint Borrower: 4 \nBorrow Request: 5 \nReturned: 6");
                p.print("Enter Value: ");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        tmp.registration();
                        break;
                    case 2:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        tmp.addNewBook(book);
                        break;
                    case 3:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        tmp.printAllBook();
                        break;
                    case 4:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        tmp.printAllBorrower(book);
                        break;
                    case 5:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        tmp.borrowRequest("", student);
                        break;
                    case 6:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        tmp.returned("bookTitle", student);
                        break;
                    default:
                        break;
                }

                // if (option == 1) {
                // // take input id, name and mobile from student
                // p.println("Registration new book");
                // } else if (option == 2) {
                // p.println("Add new book");
                // }
                // if (option == 3) {
                // p.println("Print Books");
                // }
                // if (option == 4) {
                // p.println("Print Borrower");
                // }
                // if (option == 5) {
                // p.println("Borrow request");
                // }
                // if (option == 6) {
                // p.println("Returned");
                // }
            }
        } catch (Exception e) {
        }
    }

}