package dataaccess;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthTests {
    private static AuthDAO myAuth;

    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        myAuth = new AuthDAO();
        Connection conn = new DatabaseManager().getConnection();
        myAuth.clear(conn);
        conn.close();
    }

    @Test
    public void clearTest() {
        AuthData myCred = new AuthData("jbou23", "123");
        try {
            myAuth.createToken(myCred.getAuth(), myCred.getName());
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        try {
            Connection conn = new DatabaseManager().getConnection();
            myAuth.clear(conn);
            conn.close();
        }
        catch (DataAccessException | SQLException exception) {
            throw new RuntimeException(exception);
        }
        AuthData retrievedToken = null;
        try {
            retrievedToken = myAuth.returnToken(myCred.getAuth());
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        assertEquals(null, retrievedToken);
    }
    @Test
    public void createAuthTest(){
        AuthData myCred =  new AuthData("jbou23", "123");
        try{
            myAuth.createToken(myCred.getAuth(), myCred.getName());
        }
        catch(DataAccessException exception){
            throw new RuntimeException(exception);
        }
    }
    @Test
    public void unableCreateDitto(){
        AuthData myCred =  new AuthData("jbou23", "123");
        try{
            myAuth.createToken(myCred.getAuth(), myCred.getName());
        }
        catch(DataAccessException exception){
            throw new RuntimeException(exception);
        }
        AuthData ditto = new AuthData("jbou23", "123");
        assertThrows(DataAccessException.class, () -> {myAuth.createToken(ditto.getAuth(), ditto.getName());});
    }
    @Test
    public void deletionTest(){
        AuthData myCred = new AuthData("jbou23", "123");
        try {
            myAuth.createToken(myCred.getAuth(), myCred.getName());
        } catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }

        try {
            myAuth.delete(myCred.getAuth());
        } catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }

        AuthData placeholder = null;
        try {
            placeholder = myAuth.returnToken(myCred.getAuth());
        } catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        assertEquals(null, placeholder);
    }

    @Test
    public void cantDelete() {
        AuthData myCred = new AuthData(null, null);
        assertDoesNotThrow(() -> {
            myAuth.delete(myCred.getAuth());
        });
    }
    @Test
    public void readingTest(){
        AuthData myCred = new AuthData("jbou23", "123");
        try {
            myAuth.createToken(myCred.getAuth(), myCred.getName());
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        AuthData placeholder = null;
        try {
            placeholder = myAuth.returnToken(myCred.getAuth());
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        assertEquals(myCred.getAuth(), placeholder.getAuth());
        assertEquals(myCred.getName(), placeholder.getName());
    }

    @Test
    public void antiReadingTest(){
        AuthData myCred = new AuthData("jbou23", "123");
        AuthData placeholder = null;
        try {
            placeholder = myAuth.returnToken(myCred.getAuth());
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        assertEquals(null, placeholder);
    }
}
