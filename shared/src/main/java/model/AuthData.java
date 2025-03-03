package model;

import java.util.Objects;
import java.util.UUID;

public class AuthData {
    private String authToken;
    private String username;
    public AuthData(String username){
        this.username = username;
        this.authToken = UUID.randomUUID().toString();
    }
    public AuthData(String username, String token){
        this.username = username;
        this.authToken = token;
    }

    public String getUsername(){

        return username;
    }

    public String getToken(){

        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthData authData = (AuthData) o;
        return Objects.equals(authToken, authData.authToken) && Objects.equals(username, authData.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "authToken='" + authToken + '\'' +
                ", userName='" + username + '\'' +
                '}';
    }
}
