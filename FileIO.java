import java.io.*;
import java.util.*;

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

class Student implements Serializable {
    private String name;
    private String ID;
    private String phone;

    public Student() {
    }

    public Student(String name, String ID, String phone) {
        this.name = name;
        this.ID = ID;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void getAll() {
        System.out.printf("%s %s %s\n", this.name, this.ID, this.phone);
    }
}

public class FileIO {
    private final String filePath = "data\\JavaFileIOtest.bin";

    public void writeObjectToFile(Object[] student) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        FileOutputStream fileOut = new FileOutputStream(filePath, true);
        ObjectOutputStream objectOut = null;
        if (fileSize == 0) {
            objectOut = new ObjectOutputStream(fileOut);
        } else {
            objectOut = new AppendingObjectOutputStream(fileOut);
        }
        try {
            System.out.println(fileSize);
            for (int i = 0; i < student.length; i++) {
                objectOut.writeObject(student[i]);
                objectOut.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectOut.close();
            fileOut.close();
        }
    }

    public void printObjectFromFile(Object[] students) throws IOException {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        try {
            while (fileIn.available() != 0) {
                Student tmp = (Student) objectIn.readObject();
                tmp.getAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileIn.close();
            objectIn.close();
        }
    }

    public static void main(String[] args) throws IOException {
        File f = new File("data");
        f.mkdir();
        File ff = new File("data\\JavaFileIOtest.bin");
        Student[] students = new Student[4];
        students[0] = new Student("ibrahim", "19CSE065", "01941688233");
        students[1] = new Student("Akash", "19CSE063", "01518455043");
        students[2] = new Student("Fahim", "19CSE060", "01989381781");
        students[3] = new Student("Abrar", "19CSE064", "01938110799");
        FileIO IO = new FileIO();
        IO.writeObjectToFile(students);
        IO.writeObjectToFile(students);
        IO.writeObjectToFile(students);
        IO.writeObjectToFile(students);
        IO.writeObjectToFile(students);
        IO.printObjectFromFile(students);
        ff.delete();
    }
}