import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

public class MainTest {
    static String database;

    @BeforeClass
    public static void fillFields(){
        database = "jdbc:sqlite:" + Main.DATABASE;
    }

    @Test(timeout = 1000)
    public void initializationFasterThanOneSecond(){
        Main.initialization(database);
    }

    @Test
    public void showListOfStudentsTest() {
        Main.initialization(database);
        Assert.assertTrue(Main.showListOfStudents());
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        Main.conn.close();
        Main.statement.close();
    }
}
