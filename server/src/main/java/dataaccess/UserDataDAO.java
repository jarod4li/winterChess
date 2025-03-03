package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class UserDataDAO implements UserDAO{
    private ArrayList<UserData> userList = new ArrayList<>();
    @Override
    public UserData getUserWithUsername(String username) throws DataAccessException {
        for (UserData user:userList) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public UserData getUserWithEmail(String email) throws DataAccessException {
        for (UserData user:userList) {
            if (user.getEmail().equals(email)){
                return user;
            }
        }
        return null;
    }

    @Override
    public void addUser(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        userList.add(user);
    }

    @Override
    public void clearAllUsers() throws DataAccessException {
        userList.clear();
    }
}
