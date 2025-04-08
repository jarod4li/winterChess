package dataaccess;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;


public class UserTests {

    private static UserDAO myUser;

    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        myUser = new UserDAO();
        Connection connection = new DatabaseManager().getConnection();
        myUser.clear(connection);
        connection.close();
    }
    @Test
    public void clearTest(){
        UserData jaredCred = new UserData("Jaredizer" , "123", "jbou234@gmail.com");
        try {
            myUser.createUser(jaredCred.getName(), jaredCred.getPassword(), jaredCred.getEmail());
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        try {
            Connection connection = new DatabaseManager().getConnection();
            myUser.clear(connection);
            connection.close();
        }
        catch (DataAccessException | SQLException exception) {
            throw new RuntimeException(exception);
        }
        UserData placeholder = null;
        try {
            placeholder = myUser.returnUser(jaredCred.getName());
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        assertEquals(null, placeholder);
    }
}