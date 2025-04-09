package ui;

import com.google.gson.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String urlBeginning = "http://localhost:";
    private int portNumber;
    private Map<String, String> listGames = new HashMap<>();
    public WebSocketFacade ws;

    public ServerFacade(int portNumber){

        this.portNumber = portNumber;
    }


    public String register(String username, String password, String email){
        try{
            UserData user = new UserData(username, password, email);

            URL url = new URL(urlBeginning + portNumber + "/user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonData = new Gson().toJson(user);
            conn.getOutputStream().write(jsonData.getBytes());

            conn.connect();

            if (conn.getResponseCode() == 200){

                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                AuthData authToken = new Gson().fromJson(reader, AuthData.class);
                return authToken.getToken();
            }
            else{
                InputStreamReader reader = new InputStreamReader(conn.getErrorStream());
                String error = JsonParser.parseReader(reader).getAsJsonObject().get("message").getAsString();

                System.out.println(error);
                return null;
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public String login(String username, String password) {
        try {
            UserData user = new UserData(username, password, null);

            URL url = new URL(urlBeginning + portNumber + "/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");

            String jsonData = new Gson().toJson(user);
            conn.getOutputStream().write(jsonData.getBytes());

            conn.connect();

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                AuthData authToken = new Gson().fromJson(reader, AuthData.class);
                return authToken.getToken();
            }
            else {
                InputStreamReader reader = new InputStreamReader(conn.getErrorStream());
                String error = JsonParser.parseReader(reader).getAsJsonObject().get("message").getAsString();

                System.out.println(error);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void logout(String token) {
        try {
            URL url = new URL(urlBeginning + portNumber + "/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", token);
            conn.connect();

            if (conn.getResponseCode() == 200) {
                System.out.println("Logged out successfully");

            }
            else {
                InputStreamReader reader = new InputStreamReader(conn.getErrorStream());
                String error = JsonParser.parseReader(reader).getAsJsonObject().get("message").getAsString();

                System.out.println(error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String createGame(String gameName, String token) {
        try {
            URL url = new URL(urlBeginning + portNumber + "/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authorization", token);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            GameData currGame = new GameData(gameName);
            String jsonData = new Gson().toJson(currGame);

            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                GameData game = new Gson().fromJson(reader, GameData.class);
                System.out.println("Game created successfully");
                return game.getGameID();
            }
            else {
                InputStreamReader reader = new InputStreamReader(conn.getErrorStream());
                String error = JsonParser.parseReader(reader).getAsJsonObject().get("message").getAsString();

                System.out.println(error);
                return error;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean joinGame(String gameID, String playerColor, String token){
        try {
            GameData game = new GameData(playerColor, gameID);
            String gameName = null;

            URL url = new URL(urlBeginning + portNumber + "/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("authorization", token);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonData = new Gson().toJson(game);
            conn.getOutputStream().write(jsonData.getBytes());
            conn.connect();



            if (conn.getResponseCode() == 200) {

                return true;
            }
            else{
                InputStreamReader reader = new InputStreamReader(conn.getErrorStream());
                String error = JsonParser.parseReader(reader).getAsJsonObject().get("message").getAsString();

                System.out.println(error);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
    public GameData[] listAllGames(String token){
        try {
            URL url = new URL(urlBeginning + portNumber + "/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", token);
            conn.setDoOutput(true);

            if (conn.getResponseCode() == 200) {

                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                record ListGames(GameData[] games){
                }
                ListGames games = new Gson().fromJson(reader, ListGames.class);
                return games.games();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public void clear() {
        try {
            URL url = new URL(urlBeginning + portNumber + "/db");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                System.out.println("DB cleared successfully!");
            } else {
                InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                String errorMessage = JsonParser.parseReader(inputStreamReader).getAsJsonObject().get("message").getAsString();
                System.out.println("Error: " + errorMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
