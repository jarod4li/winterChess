package ui;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Scanner;
// this one prints the board. look at bottom of code
public class PostLogin {
    private final static Scanner scanner = new Scanner(System.in);
    private static ChessGame.TeamColor teamColor;
    private static ServerFacade serverFacade;


    public static void postLogin(String token, Integer portNumber, ServerFacade serverFacade){
        PostLogin.serverFacade = serverFacade;

        String gameName;
        String gameID;
        String playerColor;

        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.println("Options:");
        System.out.println("Create");
        System.out.println("Join");
        System.out.println("List");
        System.out.println("Help");
        System.out.println("Logout");

        while (true){
            String input = scanner.nextLine().toLowerCase();
            switch (input) {
                case "create" -> {
                    System.out.println("Enter game name:");
                    gameName = scanner.nextLine();
                    gameID = serverFacade.createGame(gameName, token);
                    if (gameID != null) {
                        postLogin(token, portNumber, serverFacade);
                    }
                }
                case "join" -> {
                    System.out.println("Enter game ID:");
                    gameID = scanner.nextLine();
                    System.out.println("Join white, black, or none?");
                    playerColor = scanner.next().toUpperCase();

                    while (!(playerColor.equals("WHITE") || playerColor.equals("BLACK") || playerColor.equals("NONE"))) {
                        System.out.println("Please choose white, black, or none.");
                        playerColor = scanner.next().toUpperCase();
                    }
                    if (playerColor.equals("NONE")) {
                        playerColor = null;
                    }
                    else if(playerColor.equals("WHITE")){
                        teamColor = ChessGame.TeamColor.WHITE;
                    }
                    else{
                        teamColor = ChessGame.TeamColor.BLACK;
                    }
                    boolean joined = serverFacade.joinGame(gameID, playerColor, token);
                    if (joined) {
                        new GameConsole(teamColor, token, gameID).runGame();

                    } else {
                        postLogin(token, portNumber, serverFacade);
                    }
                    break;
                }
                case "list" -> {
                    GameData[] games = serverFacade.listAllGames(token);
                    if (games.length == 0){
                        System.out.println("No games created yet... be the first!");
                    }
                    for (GameData game: games) {
                        System.out.println("Game ID: " + game.getGameID());
                        System.out.println("Game Name: " + game.getName());
                        System.out.println("Black player: " + game.getBlack());
                        System.out.println("White player :" + game.getWhite());
                    }
                    postLogin(token, portNumber, serverFacade);
                }
                case "help" -> {
                    System.out.println("Type one of the options.");
                    postLogin(token, portNumber, serverFacade);
                }
                case "logout" -> {
                    serverFacade.logout(token);
                    PreLogin.preLogin(portNumber);
                }
                default -> postLogin(token, portNumber, serverFacade);
            }
        }


    }
    public static ServerFacade getServerFacade(){
        return serverFacade;
    }
}
