import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
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
        Dir.put("borrowersPath", "data\\borrowers.bin");
    }

    public void updateBookRequest(Student student, String bookTitle) throws IOException {
        HashMap<String, ArrayList<Book>> hashData = new HashMap<String, ArrayList<Book>>();
        File file = new File(Dir.get("borrowersPath"));
        long fileSize = file.length();
        FileOutputStream fileOut = new FileOutputStream(Dir.get("borrowersPath"), true);
        ObjectOutputStream objectOut = null;
        if (fileSize == 0) {
            objectOut = new ObjectOutputStream(fileOut);
            fileOut.close();
            objectOut.close();
        } else {
            objectOut = new AppendingObjectOutputStream(fileOut);
            fileOut.close();
            objectOut.close();
        }
        try {
            FileInputStream fileIn = new FileInputStream(Dir.get("borrowersPath"));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            ArrayList<Book> reqBook = new ArrayList<Book>();
            Book reqTmp = new Book(bookTitle, 1);
            reqTmp.name = student.name;
            reqTmp.id = student.id;
            reqTmp.phone = student.phone;
            reqBook.add(reqTmp);
            hashData.put(student.id, reqBook);
            try {
                if (file.exists() && file.isFile()) {
                    while (fileIn.available() != 0) {
                        Book tmp = (Book) objectIn.readObject();
                        ArrayList<Book> tmpBooks = hashData.get(tmp.id);
                        if (tmpBooks == null)
                            tmpBooks = new ArrayList<Book>();
                        tmpBooks.add(tmp);
                        hashData.put(tmp.id, tmpBooks);
                    }
                }
            } catch (Exception e) {
            } finally {
                fileIn.close();
                objectIn.close();
                // delete previous data file
                file.delete();
            }
            try {
                // Over-write all the data again with the new data
                FileOutputStream fileOut2 = new FileOutputStream(Dir.get("borrowersPath"), true);
                ObjectOutputStream objectOut2 = new ObjectOutputStream(fileOut2);
                try {
                    for (Map.Entry<String, ArrayList<Book>> entry : hashData.entrySet()) {
                        ArrayList<Book> value = entry.getValue();
                        for (int i = 0; i < value.size(); i++) {
                            objectOut2.writeObject(value.get(i));
                            // System.out.println(value.size());
                        }
                    }
                } catch (Exception e) {
                } finally {
                    fileOut2.close();
                    objectOut2.close();
                }
            } catch (Exception e) {
            } finally {
                // fileOut.close();
                // objectOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // objectOut.close();
            // fileOut.close();
        }
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
            // If write Book data then insert uniquely
            if (path.equals("booksPath")) {
                FileInputStream fileIn = new FileInputStream(Dir.get(path));
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                ArrayList<Book> tmpData = new ArrayList<Book>();
                tmpData.add((Book) obj);
                try {
                    if (file.exists() && file.isFile()) {
                        while (fileIn.available() != 0) {
                            Book tmp = (Book) objectIn.readObject();
                            if (tmp.bookTitle.equalsIgnoreCase(tmpData.get(0).bookTitle)) {
                                continue;
                            }
                            tmpData.add(tmp);
                        }
                    }
                } catch (Exception e) {
                } finally {
                    fileIn.close();
                    objectIn.close();
                    fileOut.close();
                    objectOut.close();
                    // delete previous data file
                    file.delete();
                }
                // Over-write all the data again with the new data
                FileOutputStream fileOut2 = new FileOutputStream(Dir.get(path), true);
                ObjectOutputStream objectOut2 = new ObjectOutputStream(fileOut2);
                try {
                    for (int i = 0; i < tmpData.size(); i++) {
                        objectOut2.writeObject(tmpData.get(i));
                        objectOut2.flush();
                    }
                } catch (Exception e) {
                } finally {
                    fileOut2.close();
                    objectOut2.close();
                }
            } else {
                objectOut.writeObject(obj);
                objectOut.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectOut.close();
            fileOut.close();
        }
    }

    public ArrayList<Book> printObjectFromFile(String path) throws IOException {
        File file = new File(Dir.get(path));
        ArrayList<Book> result = new ArrayList<Book>();
        if (file.exists() && file.isFile()) {
            FileInputStream fileIn = new FileInputStream(Dir.get(path));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            try {
                while (fileIn.available() != 0) {
                    if (path.equals("studentsPath")) {
                        Student tmp = (Student) objectIn.readObject();
                        Book tmp2 = new Book();
                        tmp2.name = tmp.name;
                        tmp2.id = tmp.id;
                        tmp2.phone = tmp.phone;
                        result.add(tmp2);
                        System.out.printf("%s %s %s\n", tmp.name, tmp.id, tmp.phone);
                    } else if (path.equals("booksPath")) {
                        Book tmp = (Book) objectIn.readObject();
                        result.add(tmp);
                        String fmt = "%-30s %s\n";
                        System.out.printf(fmt, tmp.bookTitle, tmp.numOfCopy);
                    } else if (path.equals("borrowersPath")) {
                        Book tmp = (Book) objectIn.readObject();
                        result.add(tmp);
                        String fmt = "%-10s %-20s %-15s %-20s %-10s\n";
                        System.out.printf(fmt, tmp.id, tmp.name, tmp.phone, tmp.bookTitle, "1");
                    }
                }
                if (result.size() == 0) {
                    System.out.println("No data found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileIn.close();
                objectIn.close();
            }
            return (result);
        } else {
            System.out.println("No data found");
            return (result);
        }
    }

    public Object isDataExistInDB(Object data, String path) throws IOException {
        File file = new File(Dir.get(path));
        if (file.isFile() && file.exists()) {
            FileInputStream fileIn = new FileInputStream(Dir.get(path));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            try {
                while (fileIn.available() != 0) {
                    if (path.equalsIgnoreCase("studentsPath")) {
                        Student check = (Student) objectIn.readObject();
                        Student tmp = (Student) data;
                        if (tmp.id.equalsIgnoreCase(check.id)) {
                            return check;
                        }
                    } else if (path.equals("booksPath")) {
                        Book check = (Book) objectIn.readObject();
                        Book tmp = (Book) data;
                        if (tmp.bookTitle.equalsIgnoreCase(check.bookTitle)) {
                            return check;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileIn.close();
                objectIn.close();
            }
            return null;
        }
        return null;
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

    public Student isStudentAlreadyExist(Student student) throws IOException {
        FileIO IO = new FileIO();
        // IO.isDataExistInDB(student, "studentsPath");
        return (Student) IO.isDataExistInDB(student, "studentsPath");
    }
}

class Book extends Student {
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

public class _19CSE065 {
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
    void printAllBorrower() throws IOException {
        System.out.println("Borrowers\n");
        FileIO IO = new FileIO();

        String fmt = "%-10s %-20s %-15s %-20s %-10s\n";
        System.out.printf(fmt, "ID", "Name", "Phone", "Book title", "Number of copy");
        System.out.printf(fmt, "-------", "--------", "-------", "----------", "---------------");
        IO.printObjectFromFile("borrowersPath");
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

        if (student.isStudentAlreadyExist(student) != null) {
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
    // Student searchStudent(Student student) throws IOException {
    // FileIO IO = new FileIO();
    // return (Student) IO.isDataExistInDB(student, "studentsPath");
    // }

    // call this method when a student requests to borrow a book
    void borrowRequest() throws IOException {
        System.out.println("Borrow request\n");
        Scanner sc = new Scanner(System.in);
        String id = "";

        System.out.print("Enter your ID: ");
        id = sc.nextLine();
        Student checkStudent = new Student("", id, "");
        checkStudent = checkStudent.isStudentAlreadyExist(checkStudent);
        if (checkStudent != null) {
            System.out.println("User registered\n");
            FileIO IO = new FileIO();
            String fmt = "%-30s %s\n";
            System.out.printf(fmt, "Book title", "Number of copy");
            System.out.printf(fmt, "---------------------", "---------------------");
            ArrayList<Book> count = IO.printObjectFromFile("booksPath");
            if (count.size() > 0) {
                String bookTitle = "";
                System.out.print("\nEnter book name: ");
                bookTitle = sc.nextLine();
                IO.updateBookRequest(checkStudent, bookTitle);
            }
        } else {
            System.out.println("User not registered");
        }

        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    // call this method when a student returns a book
    void returned() {
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
            _19CSE065 run = new _19CSE065();
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
                        run.printAllBorrower();
                        break;
                    case 5:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.borrowRequest();
                        break;
                    case 6:
                        p.print("\033[H\033[2J");
                        System.out.flush();
                        run.returned();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
        }
    }

}