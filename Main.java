import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

class Student implements Serializable {
	String ID;
	String name;
	String mobile;

	Student() {
		this.ID = "";
		this.name = "";
		this.mobile = "";
	}

	Student(String name, String ID, String mobile) {
		this.ID = ID;
		this.name = name;
		this.mobile = mobile;
	}
}

class Book extends Student {
	int numberOfCopy;
	String bookTitle;

	Book() {
		this.bookTitle = "";
		this.numberOfCopy = 0;
	}

	Book(String bookTitle, int numberOfCopy) {
		this.bookTitle = bookTitle;
		this.numberOfCopy = numberOfCopy;

	}
}

class Borrower extends Book {
	Borrower() {
		this.ID = "";
		this.name = "";
		this.mobile = "";
		this.bookTitle = "";
		this.numberOfCopy = 0;
	}

	Borrower(Student student, Book book) {
		this.ID = student.ID;
		this.name = student.name;
		this.mobile = student.mobile;
		this.bookTitle = book.bookTitle;
		this.numberOfCopy = 1;
	}
}

class InputSystem {
	Scanner input = new Scanner(System.in);
	String frmt = "%-25s: ";

	public String inpBookTitle() {
		System.out.printf(frmt, "Enter Book Title");
		String booktitle = input.nextLine();
		return booktitle;
	}

	public int inpNumberOfCopy() {
		System.out.printf(frmt, "Enter Number Of Copy");
		int numcopy = 0;
		try {
			numcopy = input.nextInt();
		} catch (Exception er) {
		} finally {
			input.nextLine();
		}
		return numcopy;
	}

	public String inpID() {
		System.out.printf(frmt, "Enter ID");
		String id = input.nextLine();
		return id;
	}

	public String inpName() {
		System.out.printf(frmt, "Enter Your Name");
		String name = input.nextLine();
		return name;
	}

	public String inpMobile() {
		System.out.printf(frmt, "Enter Mobile Number");
		String mobile = input.nextLine();
		System.out.flush();
		return mobile;
	}
}

public class Main {
	Scanner input = new Scanner(System.in);
	String studentsPath = "studentsPath";
	String booksPath = "booksPath";
	String borrowersPath = "borrowersPath";
	HashMap<String, String> Dir = new HashMap<String, String>() {
		{
			put("studentsPath", "data\\students.bin");
			put("booksPath", "data\\books.bin");
			put("borrowersPath", "data\\borrowers.bin");
		}
	};
	InputSystem getData = new InputSystem();
	ArrayList<Book> bookList = new ArrayList<>();
	ArrayList<Student> studentList = new ArrayList<>();
	ArrayList<Borrower> borrowerList = new ArrayList<>();

	void fetchDataFromFile(String path) throws IOException {
		File file = new File(Dir.get(path));
		if (file.exists() && file.isFile()) {
			FileInputStream fileIn = new FileInputStream(Dir.get(path));
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			try {
				while (fileIn.available() != 0) {
					if (path.equals(studentsPath)) {
						Student tmp = (Student) objectIn.readObject();
						studentList.add(tmp);
					} else if (path.equals(booksPath)) {
						Book tmp = (Book) objectIn.readObject();
						bookList.add(tmp);
					} else if (path.equals(borrowersPath)) {
						Borrower tmp = (Borrower) objectIn.readObject();
						borrowerList.add(tmp);
					}
				}
			} catch (Exception e) {
				System.out.println("\nMaybe data files corrupted.");
				System.out.println("Delete data files and run program again\n");
			} finally {
				fileIn.close();
				objectIn.close();
			}
		}
	}

	void writeDataInFile(String path) throws IOException {
		File file = new File(Dir.get(path));
		file.delete();
		FileOutputStream fileOut = new FileOutputStream(Dir.get(path), true);
		ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
		try {
			if (path.equals(studentsPath)) {
				for (Student student : studentList) {
					objectOut.writeObject(student);
					objectOut.flush();
				}
			} else if (path.equals(booksPath)) {
				for (Book book : bookList) {
					objectOut.writeObject(book);
					objectOut.flush();
				}
			} else if (path.equals(borrowersPath)) {
				for (Borrower borro : borrowerList) {
					objectOut.writeObject(borro);
					objectOut.flush();
				}
			}
		} catch (Exception e) {
		} finally {
			fileOut.close();
			objectOut.close();
		}
	}

	public Student isStudentExist(Student student) {
		for (int i = 0; i < studentList.size(); i++) {
			if (student.ID.equalsIgnoreCase(studentList.get(i).ID)) {
				return studentList.get(i);
			}
		}
		return null;
	}

	public Book isBookExist(Book book) {
		for (int i = 0; i < bookList.size(); i++) {
			if (book.bookTitle.equalsIgnoreCase(bookList.get(i).bookTitle)) {
				return bookList.get(i);
			}
		}
		return null;
	}

	// add a new Book in the library
	public void addNewBook() throws IOException {
		System.out.println();
		String booktitle = getData.inpBookTitle();
		int numcopy = 0;
		numcopy = getData.inpNumberOfCopy();
		if (numcopy == 0) {
			System.out.println("\nInput must be integer or greater than zero\nPlease, try again.\n");
			return;
		}
		Book book = new Book(booktitle, numcopy);
		for (int i = 0; i < bookList.size(); i++) {
			if (book.bookTitle.equalsIgnoreCase(bookList.get(i).bookTitle)) {
				bookList.get(i).numberOfCopy = book.numberOfCopy;
				writeDataInFile(booksPath);
				System.out.println("\nUpdate successfully\n");
				return;
			}
		}
		bookList.add(book);
		writeDataInFile(booksPath);
		System.out.println("\nAdd successfully\n");
	}

	// register a student if he/she is not registered before
	public void registration() throws IOException {
		System.out.println();
		String id = getData.inpID();
		String name = getData.inpName();
		String mobile = getData.inpMobile();
		Student student = new Student(name, id, mobile);
		if (isStudentExist(student) != null) {
			System.out.println("\nStudent Already Registered\n");
		} else {
			studentList.add(student);
			writeDataInFile(studentsPath);
			System.out.println("\nregistered successfully\n");
		}
	}

	// print all the books in the library
	public void printAllBook() {
		System.out.println();
		if (bookList.size() > 0) {
			System.out.printf("%-30s %s\n", "Book Title", "Number of copy");
			System.out.printf("%-30s %s\n", "-------------------------", "-------------------");
			for (Book book : bookList) {
				System.out.printf("%-30s %s\n", book.bookTitle, book.numberOfCopy);
			}
			System.out.println();
		} else {
			System.out.println("\nNo books are available!\n");
		}
	}

	void printAllBorrower() {
		if (borrowerList.size() > 0) {
			String frmt = "%-10s %-20s %-15s %-30s %-10s\n";
			System.out.println();
			System.out.printf(frmt, "ID", "Student Name", "Mobile", "Book Title", "Number Of Copy");
			System.out.printf(frmt, "---------", "----------------", "------------", "---------------------------",
					"---------------");
			for (Borrower borrower : borrowerList) {
				System.out.printf(frmt, borrower.ID, borrower.name, borrower.mobile, borrower.bookTitle,
						borrower.numberOfCopy);
			}
			System.out.println();
		} else {
			System.out.println("\nThere are no borrower.\n");
		}
	}

	// call this method when a student requests to borrow a book
	public void borrowRequest() throws IOException {
		System.out.println();
		String ID = getData.inpID();
		Student student = new Student("", ID, "");
		student = isStudentExist(student);
		if (student != null) {
			printAllBook();
			String bookTitle = getData.inpBookTitle();
			for (Book book : bookList) {
				if (bookTitle.equalsIgnoreCase(book.bookTitle) && book.numberOfCopy > 0) {
					System.out.println("\nBook borrered successfully\n");
					book.numberOfCopy--;
					writeDataInFile(booksPath);
					for (Borrower borro : borrowerList) {
						if (student.ID.equalsIgnoreCase(borro.ID) && bookTitle.equalsIgnoreCase(borro.bookTitle)) {
							borro.numberOfCopy++;
							writeDataInFile(borrowersPath);
							return;
						}
					}
					Borrower borrower = new Borrower(student, book);
					borrowerList.add(borrower);
					writeDataInFile(borrowersPath);
					return;
				}
			}
			System.out.println("\nThis Book is not available in the library\n");
		} else {
			System.out.println("\nStudent ID not registered\n");
		}
	}

	// call this method when a student returns a book
	public void returned() throws IOException {
		System.out.println();
		String ID = getData.inpID();
		Student student = new Student("", ID, "");
		student = isStudentExist(student);
		if (student != null) {
			boolean isReturned = false;
			// pupose of using iterator to delete element from ArrayList while iterating
			Iterator<Borrower> itr = borrowerList.iterator();
			while (itr.hasNext()) {
				Borrower borro = itr.next();
				if (student.ID.equalsIgnoreCase(borro.ID)) {
					for (Book book : bookList) {
						if (borro.bookTitle.equalsIgnoreCase(book.bookTitle)) {
							book.numberOfCopy += borro.numberOfCopy;
							writeDataInFile(booksPath);
							itr.remove();
							break;
						}
					}
					isReturned = true;
					writeDataInFile(borrowersPath);
				}
			}
			if (isReturned) {
				System.out.println("\nAll books returned successfully\n");
				return;
			} else
				System.out.println("\nYou do not have any books to return.\n");
		} else {
			System.out.println("\nStudent ID not registered\n");
		}
	}

	public static void main(String[] args) throws IOException {
		File dataPath = new File("data");
		dataPath.mkdir();
		int option = 0;
		Main run = new Main();

		run.fetchDataFromFile(run.studentsPath);
		run.fetchDataFromFile(run.booksPath);
		run.fetchDataFromFile(run.borrowersPath);

		while (true) {
			System.out.println("Registration: 1");
			System.out.println("Add New Book: 2");
			System.out.println("Print Books: 3");
			System.out.println("Print Borrower: 4");
			System.out.println("Borrow Request: 5");
			System.out.println("Returned: 6");
			System.out.println("Terminate: 7");
			System.out.print("\nEnter Value: ");
			try {
				option = run.input.nextInt();
			} catch (Exception er) {
				System.out.println("\nInput must be in this (1-7) range\nTry again\n");
				continue;
			} finally {
				run.input.nextLine();
			}
			if (option == 1) {
				run.registration();
			} else if (option == 2) {
				run.addNewBook();
			} else if (option == 3) {
				run.printAllBook();
			} else if (option == 4) {
				run.printAllBorrower();
			} else if (option == 5) {
				run.borrowRequest();
			} else if (option == 6) {
				run.returned();
			} else if (option == 7) {
				return;
			} else {
				System.out.println("\nWrong command");
				System.out.println("Try again\n");
			}
		}
	}
}