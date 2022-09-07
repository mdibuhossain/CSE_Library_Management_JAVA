import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

class Print {
    void println(String st) {
        System.out.println(st);
    }

    void print(String st) {
        System.out.print(st);
    }
}

class AppendingObjectOutputStream extends ObjectOutputStream {
    public AppendingObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        // do not write a header, but reset:
        // this line added after another question
        // showed a problem with the original
        reset();
    }
}

class FileIO {
    public HashMap<String, String> Dir = new HashMap<String, String>();

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
            objectOut.writeObject(obj);
            objectOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectOut.close();
            fileOut.close();
        }
    }

    public void printObjectFromFile(String path) throws IOException {
        File file = new File(Dir.get(path));
        if (file.exists() && file.isFile()) {
            FileInputStream fileIn = new FileInputStream(Dir.get(path));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            try {
                int count = 0;
                while (fileIn.available() != 0) {
                    if (path.equals("studentsPath")) {
                        Student tmp = (Student) objectIn.readObject();
                        // tmp.getAll();
                        System.out.printf("%s %s %s\n", tmp.name, tmp.id, tmp.phone);
                        count++;
                    } else if (path.equals("booksPath")) {
                        Book tmp = (Book) objectIn.readObject();
                        // tmp.getAll();
                        String fmt = "%-30s %s\n";
                        System.out.printf(fmt, tmp.bookTitle, tmp.numOfCopy);
                        count++;
                    }
                }
                if (count == 0) {
                    System.out.println("No data found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileIn.close();
                objectIn.close();
            }
        } else {
            System.out.println("No data found");
        }
    }

    public boolean isDataExistInDB(Object data, String path) throws IOException {
        File file = new File(Dir.get(path));
        if (file.isFile() && file.exists()) {
            FileInputStream fileIn = new FileInputStream(Dir.get(path));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            try {
                while (fileIn.available() != 0) {
                    if (path.equals("studentsPath")) {
                        Student check = (Student) objectIn.readObject();
                        Student tmp = (Student) data;
                        if (tmp.id.equalsIgnoreCase(check.id)) {
                            return true;
                        }
                    } else if (path.equals("booksPath")) {
                        Book check = (Book) objectIn.readObject();
                        Book tmp = (Book) data;
                        if (tmp.bookTitle.equalsIgnoreCase(check.bookTitle)) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileIn.close();
                objectIn.close();
            }
            return false;
        }
        return false;
    }

}

class Student implements Serializable {
    public String name;
    public String id;
    public String phone;

    public Student() {
        name = id = phone = "";
    }

    public Student(String name, String id, String phone) {
        this.name = name;
        this.id = id;
        this.phone = phone;
    }

    public boolean isStudentAlreadyExist(Student student) throws IOException {
        FileIO IO = new FileIO();
        // IO.isDataExistInDB(student, "studentsPath");
        if (IO.isDataExistInDB(student, "studentsPath") == true)
            return true;
        return false;
    }
}

class Book implements Serializable {
    String bookTitle;
    int numOfCopy;

    Book() {
        bookTitle = "";
        numOfCopy = 0;
    }

    Book(String bookTitle, int numOfCopy) {
        this.bookTitle = bookTitle;
        this.numOfCopy = numOfCopy;
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
    void addNewBook() throws IOException {
        System.out.println("Add new book\n");

        Scanner sc = new Scanner(System.in);
        String bookTitle = "";
        int numOfCopy = 0;

        System.out.printf("%-20s", "Book title:");
        while (bookTitle.length() == 0)
            bookTitle = sc.nextLine();

        System.out.printf("%-20s", "Number of copy:");
        try {
            numOfCopy = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\nNumber of copy field should integer number\nTry again");
            try {
                System.in.read();
            } catch (Exception er) {
            }
            return;
        }

        Book book = new Book(bookTitle, numOfCopy);
        FileIO IO = new FileIO();
        IO.writeObjectToFile(book, "booksPath");

        System.out.println("Successfully Added");
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
    void printAllBook() throws IOException {
        // implement this method
        System.out.println("Available Books\n");
        FileIO IO = new FileIO();

        String fmt = "%-30s %s\n";
        System.out.printf(fmt, "Book title", "Number of copy");
        System.out.printf(fmt, "---------------------", "---------------------");
        IO.printObjectFromFile("booksPath");
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
        System.out.println("Registration new book\n");
        Scanner sc = new Scanner(System.in);

        String name = "";
        String id = "";
        String phone = "";

        System.out.printf("%-20s", "ID:");
        while (id.length() == 0) {
            id = sc.nextLine();
        }

        System.out.printf("%-20s", "Name:");
        while (name.length() == 0) {
            name = sc.nextLine();
        }

        System.out.printf("%-20s", "Phone:");
        while (phone.length() == 0) {
            phone = sc.nextLine();
        }

        Student student = new Student(name, id, phone);

        if (student.isStudentAlreadyExist(student) == true) {
            System.out.println("Student ID already exist");
        } else {
            FileIO IO = new FileIO();
            // IO.printObjectFromFile("studentsPath");
            IO.writeObjectToFile(student, "studentsPath");
            System.out.println("Successfully registered");
        }
        try {
            System.in.read();
        } catch (Exception e) {
        }

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
            Run run = new Run();
            Student student = new Student();
            Book book = new Book();
            Print p = new Print();
            while (true) {
                p.print("\033[H\033[2J");
                System.out.flush();
                p.println("1. Registration");
                p.println("2. Add New Book");
                p.println("3. Print Books");
                p.println("4. Print Borrower");
                p.println("5. Borrow Request");
                p.println("6. Returned\n");
                p.print("Enter Value: ");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.registration();
                        break;
                    case 2:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.addNewBook();
                        break;
                    case 3:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.printAllBook();
                        break;
                    case 4:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.printAllBorrower(book);
                        break;
                    case 5:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.borrowRequest("", student);
                        break;
                    case 6:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.returned("bookTitle", student);
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