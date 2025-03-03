package dataaccess;

import model.*;


public interface UserDAO {
    public UserData getUserWithUsername(String username) throws DataAccessException;
    public UserData getUserWithEmail(String email) throws  DataAccessException;
    public void addUser(String username, String password, String email) throws DataAccessException;
    public void clearAllUsers() throws DataAccessException;
}
