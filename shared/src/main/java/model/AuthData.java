package model;
public class AuthData {
    private String authToken;
    private String username;
    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }
    public String getAuth() {
        return authToken;
    }
    public void setAuth(String authToken) {
        this.authToken = authToken;
    }
    public String getName() {
        return username;
    }
    public void setName(String username) {
        this.username = username;
    }

}