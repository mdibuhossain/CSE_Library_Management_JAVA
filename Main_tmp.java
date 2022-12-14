import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
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

class CursorBreak {
    void pause() {
        System.out.println("\nPress any key to continue / back....");
        try {
            System.in.read();
        } catch (Exception er) {
        }
    }
}

class AppendingObjectOutputStream extends ObjectOutputStream {
    public AppendingObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        reset();
    }
}

class FileIO {
    Print p = new Print();

    public HashMap<String, String> Dir = new HashMap<String, String>();

    // all path list, using hashMap to store
    public FileIO() {
        Dir.put("studentsPath", "data\\students.bin");
        Dir.put("booksPath", "data\\books.bin");
        Dir.put("borrowersPath", "data\\borrowers.bin");
    }

    public void updateBookList(Book book, boolean isIncrease) throws IOException {
        FileIO IO = new FileIO();
        book = (Book) IO.isDataExistInDB(book, "booksPath");
        if (isIncrease)
            book.numOfCopy++;
        else
            book.numOfCopy--;
        IO.writeObjectToFile(book, "booksPath");
    }

    public void updateBookRequest(Student student, String bookTitle, boolean isReturn) throws IOException {
        // If it is not for return, then run this
        if (!isReturn) {
            // at first, check whether data is available or not
            FileIO IO = new FileIO();
            Book checkingBook = new Book();
            checkingBook.bookTitle = bookTitle;
            checkingBook = (Book) IO.isDataExistInDB(checkingBook, "booksPath");
            if (!(checkingBook != null && checkingBook.bookTitle.equalsIgnoreCase(bookTitle)
                    && checkingBook.numOfCopy > 0)) {
                p.println("This book is not available right now!");
                try {
                    System.in.read();
                } catch (Exception e) {
                }
                return;
            }
            // also update booklist from DB
            updateBookList(checkingBook, false);
        }

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
            if (!isReturn) {
                ArrayList<Book> reqBook = new ArrayList<Book>();
                Book reqTmp = new Book(bookTitle, 1);
                reqTmp.name = student.name;
                reqTmp.id = student.id;
                reqTmp.phone = student.phone;
                reqBook.add(reqTmp);
                hashData.put(student.id, reqBook);
            }
            try {
                if (file.exists() && file.isFile()) {
                    while (fileIn.available() != 0) {
                        Book tmp = (Book) objectIn.readObject();
                        ArrayList<Book> tmpBooks = hashData.get(tmp.id);
                        if (tmpBooks == null)
                            tmpBooks = new ArrayList<Book>();
                        if (isReturn && student.id.equalsIgnoreCase(tmp.id)) {
                            FileIO IO = new FileIO();
                            IO.updateBookList(tmp, true);
                            continue;
                        }
                        tmpBooks.add(tmp);
                        hashData.put(tmp.id, tmpBooks);
                    }
                }
            } catch (Exception e) {
            } finally {
                fileIn.close();
                objectIn.close();
                // delete previous data file for over write new data
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
                        }
                    }
                } catch (Exception e) {
                } finally {
                    fileOut2.close();
                    objectOut2.close();
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    // delete previous data file for over write new data
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

    public ArrayList<Book> getObjectsFromFile(String path, boolean print) throws IOException {
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
                        if (print)
                            System.out.printf("%s %s %s\n", tmp.name, tmp.id, tmp.phone);
                    } else if (path.equals("booksPath")) {
                        Book tmp = (Book) objectIn.readObject();
                        result.add(tmp);
                        String fmt = "%-30s %s\n";
                        if (print)
                            System.out.printf(fmt, tmp.bookTitle, tmp.numOfCopy);
                    } else if (path.equals("borrowersPath")) {
                        Book tmp = (Book) objectIn.readObject();
                        result.add(tmp);
                        String fmt = "%-10s %-20s %-15s %-20s %-10s\n";
                        if (print)
                            System.out.printf(fmt, tmp.id, tmp.name, tmp.phone, tmp.bookTitle, "1");
                    }
                }
                if (result.size() == 0 && print) {
                    p.println("No data found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileIn.close();
                objectIn.close();
            }
            return (result);
        } else {
            if (print)
                p.println("No data found");
            return (result);
        }
    }

    public Object isDataExistInDB(Object data, String path) throws IOException {
        FileIO IO = new FileIO();
        ArrayList<Book> fetchData = IO.getObjectsFromFile(path, false);
        if (fetchData.size() > 0) {
            if (path.equalsIgnoreCase("studentsPath")) {
                for (int i = 0; i < fetchData.size(); i++) {
                    Student tmp = (Student) data;
                    if (tmp.id.equalsIgnoreCase(fetchData.get(i).id)) {
                        Student result = new Student();
                        result.id = fetchData.get(i).id;
                        result.name = fetchData.get(i).name;
                        result.phone = fetchData.get(i).phone;
                        return result;
                    }
                }
                return null;
            } else if (path.equalsIgnoreCase("booksPath")) {
                for (int i = 0; i < fetchData.size(); i++) {
                    Book tmp = (Book) data;
                    if (tmp.bookTitle.equalsIgnoreCase(fetchData.get(i).bookTitle)) {
                        return fetchData.get(i);
                    }
                }
                return null;
            } else if (path.equalsIgnoreCase("borrowersPath")) {
                for (int i = 0; i < fetchData.size(); i++) {
                    Book tmp = (Book) data;
                    if (tmp.id.equalsIgnoreCase(fetchData.get(i).id)) {
                        return fetchData.get(i);
                    }
                }
                return null;
            }
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

public class Main_tmp {
    Print p = new Print();
    CursorBreak br = new CursorBreak();

    // add a new Book in the library
    void addNewBook() throws IOException {
        p.println("Add new book\n");

        Scanner sc = new Scanner(System.in);
        String bookTitle = "";
        int numOfCopy = 0;

        System.out.printf("%-30s: ", "Book title (case insensitive)");
        while (bookTitle.length() == 0)
            bookTitle = sc.nextLine();

        System.out.printf("%-30s: ", "Number of copy");
        try {
            numOfCopy = sc.nextInt();
        } catch (InputMismatchException e) {
            p.println("\nNumber of copy field should integer number\nTry again");
            br.pause();
            return;
        }

        Book book = new Book(bookTitle, numOfCopy);
        FileIO IO = new FileIO();
        IO.writeObjectToFile(book, "booksPath");

        p.println("\nSuccessfully Added");
        br.pause();
    }

    // search a book in the library whether it is avilable or not
    Book searchBook(String bookTitle) {
        // implement this method
        return null;
    }

    // print all the books in the library
    void printAllBook() throws IOException {
        // implement this method
        p.println("Available Books\n");
        FileIO IO = new FileIO();

        String fmt = "%-30s %s\n";
        System.out.printf(fmt, "Book title", "Number of copy");
        System.out.printf(fmt, "---------------------", "---------------------");
        IO.getObjectsFromFile("booksPath", true);
        br.pause();
    }

    // returns the borrower list of this book
    void printAllBorrower() throws IOException {
        p.println("Borrowers\n");
        FileIO IO = new FileIO();

        String fmt = "%-10s %-20s %-15s %-20s %-10s\n";
        System.out.printf(fmt, "ID", "Name", "Phone", "Book title", "Number of copy");
        System.out.printf(fmt, "-------", "--------", "-------", "----------", "---------------");
        IO.getObjectsFromFile("borrowersPath", true);
        br.pause();
    }

    // register a student if he/she is not registered before
    void registration() throws IOException {
        // Print p = new Print();
        // String fmt = "%1$4s %2$10s %3$10s%n";
        p.println("Registration new book\n");
        Scanner sc = new Scanner(System.in);

        String name = "";
        String id = "";
        String phone = "";

        System.out.printf("%-25s: ", "ID (case insensitive)");
        while (id.length() == 0) {
            id = sc.nextLine();
        }

        System.out.printf("%-25s: ", "Name");
        while (name.length() == 0) {
            name = sc.nextLine();
        }

        System.out.printf("%-25s: ", "Phone");
        while (phone.length() == 0) {
            phone = sc.nextLine();
        }

        Student student = new Student(name, id, phone);

        if (student.isStudentAlreadyExist(student) != null) {
            p.println("\nStudent ID already exist");
        } else {
            FileIO IO = new FileIO();
            // IO.getObjectsFromFile("studentsPath");
            IO.writeObjectToFile(student, "studentsPath");
            p.println("\nSuccessfully registered");
        }
        br.pause();

    }

    // search the student using studentID
    // Student searchStudent(Student student) throws IOException {
    // FileIO IO = new FileIO();
    // return (Student) IO.isDataExistInDB(student, "studentsPath");
    // }

    // call this method when a student requests to borrow a book
    void borrowRequest() throws IOException {
        p.println("Borrow request\n");
        Scanner sc = new Scanner(System.in);
        String id = "";

        p.print("Enter your ID (case insensitive): ");
        id = sc.nextLine();
        Student checkStudent = new Student("", id, "");
        checkStudent = checkStudent.isStudentAlreadyExist(checkStudent);
        if (checkStudent != null) {
            p.println("\nID recognized\n");
            FileIO IO = new FileIO();
            String fmt = "%-30s %s\n";
            System.out.printf(fmt, "Book title", "Number of copy");
            System.out.printf(fmt, "---------------------", "---------------------");
            ArrayList<Book> count = IO.getObjectsFromFile("booksPath", true);
            if (count.size() > 0) {
                String bookTitle = "";
                p.print("\nEnter book name: ");
                bookTitle = sc.nextLine();
                IO.updateBookRequest(checkStudent, bookTitle, false);
            }
        } else {
            p.println("User not registered");
        }

        br.pause();
    }

    // call this method when a student returns a book
    void returned() throws IOException {
        Scanner sc = new Scanner(System.in);
        p.println("Return books\n");

        String id = "";
        p.print("Enter your ID (case insensitive): ");
        while (id.length() == 0) {
            id = sc.nextLine();
        }
        Student student = new Student("", id, "");
        student = student.isStudentAlreadyExist(student);
        if (student != null) {
            p.println("ID recognized");
            FileIO IO = new FileIO();
            IO.updateBookRequest(student, "", true);
            p.println("\nand Books returned successfully");

        } else {
            p.println("ID is not registered");
        }

        br.pause();
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            File dataPath = new File("data");
            dataPath.mkdir();
            int option;
            Main_tmp run = new Main_tmp();
            Print p = new Print();
            while (true) {
                // This is for clear screen
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                p.println("1. Registration");
                p.println("2. Add New Book");
                p.println("3. Print Books");
                p.println("4. Print Borrower");
                p.println("5. Borrow Request");
                p.println("6. Returned");
                p.println("7. Exit\n");
                p.print("Enter Value: ");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        run.registration();
                        break;
                    case 2:
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        run.addNewBook();
                        break;
                    case 3:
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        run.printAllBook();
                        break;
                    case 4:
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        run.printAllBorrower();
                        break;
                    case 5:
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        run.borrowRequest();
                        break;
                    case 6:
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        run.returned();
                        break;
                    case 7:
                        return;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
        }
    }

}