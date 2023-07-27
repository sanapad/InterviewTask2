import java.sql.*;
import java.util.Scanner;

public class Main {
    public final static String DATABASE = "db.sqlite";
    public static Connection conn = null;
    public static Statement statement = null;

    public static void main(String[] args) {

        initialization("jdbc:sqlite:" + DATABASE);

        chooseAction();

        closeConnection();
    }

    public static void chooseAction() {
        Scanner scan = new Scanner(System.in);

        System.out.println("\nChoose action:");
        System.out.println("0. exit program");
        System.out.println("1. add a new student");
        System.out.println("2. delete a student by id");
        System.out.println("3. show the list of students");

        String value = scan.nextLine();

        switch (value) {
            case "0":
                break;
            case "1":
                addStudent();
                chooseAction();
                break;
            case "2":
                deleteStudentByID();
                chooseAction();
                break;
            case "3":
                showListOfStudents();
                chooseAction();
                break;
            default:
                System.out.println("unknown operation");
                chooseAction();
        }
        scan.close();
    }

    public static void initialization(String url) {

        try {
            conn = DriverManager.getConnection(url);

            statement = conn.createStatement();

            statement.execute("""
                    create table if not exists 'students'
                    (
                        id             integer
                            constraint students_pk
                                primary key autoincrement,
                        firstname      text not null,
                        lastname       text not null,
                        patronymic     text,
                        group_students text not null,
                        birthday       text not null
                    );

                    """);


            System.out.println("Connection to the database 'db.sqlite' has been established.");

        } catch (SQLException e) {
            System.out.println("Error: connection to the database 'db.sqlite' could not be established.");
            System.out.println(e.getMessage());
        }

    }

    public static void closeConnection() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void addStudent() {
        String firstname, lastname, patronymic, group_students, birthday;

        Scanner scan = new Scanner(System.in);

        firstname = safeInput(scan, "Type student's name (required field): ");
        lastname = safeInput(scan, "Type student's lastname (required field): ");

        System.out.println("Type student's patronymic (not required): ");
        patronymic = scan.nextLine();

        group_students = safeInput(scan, "Type student's group_students (required field): ");
        birthday = safeInput(scan, "Type student's birthday (required field): ");

        String query = "INSERT INTO students (firstname, lastname, patronymic, group_students, birthday) VALUES ('" + firstname +
                "', '" + lastname + "', '" + patronymic + "', '" + group_students + "' , '" + birthday + "')";

        try {
            statement.execute(query);
            System.out.println("Student has been added");
        } catch (SQLException e) {
            System.out.println("Error: when executing a request to add a student");
        }

    }

    private static String safeInput(Scanner scan, String inputDescription) {
        String resultInput;

        do {
            System.out.println(inputDescription);
            resultInput = scan.nextLine();
        }
        while (resultInput.isEmpty());

        return resultInput;
    }

    public static void deleteStudentByID() {
        Scanner scan = new Scanner(System.in);

        String deletingID = safeInput(scan, "Type the id of the student to be deleted: ");

        try {
            // if such ID exists
            ResultSet resultSet = statement.executeQuery("SELECT * FROM students WHERE id=" + deletingID);
            resultSet.next();
            resultSet.getString("id");

            statement.execute("DELETE FROM students WHERE id=" + deletingID);
            System.out.println("Student with id = " + deletingID + " has been deleted");
        } catch (SQLException e) {
            System.out.println("There is no such student.");
        }

    }

    public static boolean showListOfStudents() {

        ResultSet rs;

        try {
            rs = statement.executeQuery("SELECT * FROM students");

            System.out.printf("%-5s", "id");
            System.out.printf("%20s", "name");
            System.out.printf("%20s", "last name");
            System.out.printf("%20s", "patronymic");
            System.out.printf("%15s", "group");
            System.out.printf("%15s", "date of birth\n");

            while (rs.next()) {
                System.out.printf("%-5s", rs.getString(1));
                System.out.printf("%20s", rs.getString(2));
                System.out.printf("%20s", rs.getString(3));
                System.out.printf("%20s", rs.getString(4));
                System.out.printf("%15s", rs.getString(5));
                System.out.printf("%15s", rs.getString(6) + "\n");
            }

            rs.close();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


}
