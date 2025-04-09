package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;
import websocket.commands.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@WebSocket
public class WebSocketHandler {

    private static final Map<String, Map<String, Session>> sessionMap = new HashMap<>();
    AuthDAO authDAO;
    GameDAO gameDAO;

    public WebSocketHandler() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();

    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand gameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch(gameCommand.getCommandType()) {
            case JOIN_PLAYER:
            case JOIN_OBSERVER:
                Join joinCommand = new Gson().fromJson(message, Join.class);
                try {

                    AuthData token = authDAO.findToken(gameCommand.getAuthString());
                    String authToken = gameCommand.getAuthString();
                    addPlayer(joinCommand.getGameID(), authToken, session);


                    if(token == null ) {
                        sendMessage(joinCommand.getGameID(), new ErrorMessage("Error: unauthorized"), authToken);
                        return;

                    }

                    String gameID = joinCommand.getGameID();


                    if(gameDAO.getGame(gameID) == null) {
                        sendMessage(joinCommand.getGameID(), new ErrorMessage("Error: gameID does not exist"), authToken);
                        return;

                    }
                    GameData game = gameDAO.getGame(gameID);

                    ChessGame.TeamColor playerColor = joinCommand.getTeamColor();
                    if (playerColor == ChessGame.TeamColor.WHITE && !(Objects.equals(game.getWhite(), token.getUsername()))){
                        sendMessage(gameID, new ErrorMessage("Error: white position is not available"), authToken);
                        return;

                    }
                    if (playerColor == ChessGame.TeamColor.BLACK && !(Objects.equals(game.getBlack(), token.getUsername()))){
                        sendMessage(gameID, new ErrorMessage("Error: black position is not available"), authToken);
                        return;
                    }

                    ChessGame gameBoard = game.getGame();
                    LoadGame notificationRootClient = new LoadGame(gameBoard);

                    Notification notification;
                    if (playerColor == null){
                        notification = new Notification(token.getUsername() + " joined as observer.");
                    }
                    else {
                        notification = new Notification(token.getUsername() + " has joined as " + playerColor);
                    }
                    sendMessage(gameID, notificationRootClient, authToken);
                    broadcastMessage(gameID, notification, authToken);


                } catch (DataAccessException e) {
                    throw e;
                }
                break;

            case MAKE_MOVE:
                MakeMove makeMove = new Gson().fromJson(message, MakeMove.class);
                try {

                    AuthData token = authDAO.findToken(gameCommand.getAuthString());
                    String authToken = gameCommand.getAuthString();
                    addPlayer(makeMove.getGameID(), makeMove.getAuthString(), session);
                    if(token == null) {
                        sendMessage(makeMove.getGameID(), new ErrorMessage("Error: Unauthorized"), authToken);
                        return;
                    }
                    if (gameDAO.getGame(makeMove.getGameID()) == null){
                        sendMessage(makeMove.getGameID(), new ErrorMessage("Error: gameID does not exist"), authToken);
                        return;
                    }
                    GameData game = gameDAO.getGame(makeMove.getGameID());


                    String userName = token.getUsername();
                    String gameID = makeMove.getGameID();
                    ChessGame gameBoard = game.getGame();
                    if (gameBoard.getResign() != null){
                        sendMessage(makeMove.getGameID(), new ErrorMessage("Error: " + gameBoard.getResign() + " already resigned"), authToken);
                        return;
                    }

                    ChessMove move = makeMove.getMove();
                    ChessPiece piece = gameBoard.getBoard().getPiece(move.getStartPosition());
                    ChessGame.TeamColor playerColor = null;


                    if (Objects.equals(game.getWhite(), userName)) {
                        playerColor = ChessGame.TeamColor.WHITE;
                    } else if (Objects.equals(game.getBlack(), userName)){
                        playerColor = ChessGame.TeamColor.BLACK;
                    }


                    try {
                        gameBoard.makeMove(move);
                    } catch (InvalidMoveException e) {
                        sendMessage(gameID, new ErrorMessage("Invalid move."), authToken);
                        return;
                    }

                    if(gameBoard.isInCheck(ChessGame.TeamColor.BLACK) || gameBoard.isInCheck(ChessGame.TeamColor.WHITE)) {
                        sendMessage(gameID, new Notification("Check!"), authToken);
                        broadcastMessage(gameID, new Notification("Check!"), authToken);
                        GameData updatedGame = new GameData(gameDAO.getGame(gameID).getName(), gameID, gameDAO.getGame(gameID).getWhite(), gameDAO.getGame(gameID).getBlack(), gameBoard);
                        gameDAO.updateGame(gameID, updatedGame);
                    }

                    if(gameBoard.isInCheckmate(ChessGame.TeamColor.BLACK) || gameBoard.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                        sendMessage(gameID, new Notification("Checkmate!"), authToken);
                        broadcastMessage(gameID, new Notification("Checkmate!"), authToken);
                        gameBoard.setTeamTurn(null);
                        LoadGame finalGame = new LoadGame(game);
                        sendMessage(gameID, finalGame, authToken);
                        broadcastMessage(gameID, finalGame, authToken);
                        GameData updatedGame = new GameData(gameDAO.getGame(gameID).getName(), gameID, gameDAO.getGame(gameID).getWhite(), gameDAO.getGame(gameID).getBlack(), gameBoard);
                        gameDAO.updateGame(gameID, updatedGame);
                        return;
                    }


                    if(gameBoard.isInStalemate(ChessGame.TeamColor.BLACK) || gameBoard.isInStalemate(ChessGame.TeamColor.WHITE)) {
                        sendMessage(gameID, new Notification("Stalemate!"), authToken);
                        broadcastMessage(gameID, new Notification("Stalemate!"), authToken);
                        gameBoard.setTeamTurn(null);
                        LoadGame finalGame = new LoadGame(game);
                        sendMessage(gameID, finalGame, authToken);
                        broadcastMessage(gameID, finalGame, authToken);
                        GameData updatedGame = new GameData(gameDAO.getGame(gameID).getName(), gameID, gameDAO.getGame(gameID).getWhite(), gameDAO.getGame(gameID).getBlack(), gameBoard);
                        gameDAO.updateGame(gameID, updatedGame);
                        return;
                    }


                    if(piece.getTeamColor() != playerColor) {
                        sendMessage(gameID, new ErrorMessage("Cannot move that piece."), authToken);
                        return;
                    }

                    GameData updatedGame = new GameData(gameDAO.getGame(gameID).getName(), gameID, gameDAO.getGame(gameID).getWhite(), gameDAO.getGame(gameID).getBlack(), gameBoard);
                    gameDAO.updateGame(gameID, updatedGame);

                    LoadGame notificationToRootClient = new LoadGame(game);


                    ChessPosition currPosition = makeMove.getMove().getEndPosition();
                    String col = Character.toString((char)currPosition.getColumn()+96);
                    Notification notification = new Notification(userName + " moved to " + col + String.valueOf(currPosition.getRow()));

                    sendMessage(gameID, notificationToRootClient, authToken);
                    broadcastMessage(gameID, notificationToRootClient, authToken);
                    broadcastMessage(gameID, notification, authToken);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
            case LEAVE:
                Leave leaveCommand = new Gson().fromJson(message, Leave.class);
                try {
                    addPlayer(leaveCommand.getGameID(), leaveCommand.getAuthString(), session);
                    AuthData token = authDAO.findToken(gameCommand.getAuthString());
                    String authToken = gameCommand.getAuthString();
                    if(token == null ) {
                        sendMessage(leaveCommand.getGameID(), new ErrorMessage("Error: Unauthorized"), authToken);
                        return;
                    }


                    String userName = token.getUsername();
                    String gameID = leaveCommand.getGameID();
                    if (Objects.equals(gameDAO.getGame(gameID).getWhite(), userName)) {
                        GameData updatedGame = new GameData(gameDAO.getGame(gameID).getName(), gameID, null, gameDAO.getGame(gameID).getBlack(), gameDAO.getGame(gameID).getGame());
                        gameDAO.updateGame(gameID, updatedGame);
                    } else if (Objects.equals(gameDAO.getGame(gameID).getBlack(), userName)){
                        GameData updatedGame = new GameData(gameDAO.getGame(gameID).getName(), gameID, gameDAO.getGame(gameID).getWhite(), null, gameDAO.getGame(gameID).getGame());
                        gameDAO.updateGame(gameID, updatedGame);
                    }
                    Notification notification = new Notification(userName + " has left the game.");
                    broadcastMessage(gameID, notification, authToken);
                    removeSessionFromGame(gameID, authToken);
                    removeSession(session);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
            case RESIGN:
                Resign resignCommand = new Gson().fromJson(message, Resign.class);
                try {
                    addPlayer(resignCommand.getGameID(), resignCommand.getAuthString(), session);
                    AuthData token = authDAO.findToken(gameCommand.getAuthString());
                    String authToken = gameCommand.getAuthString();
                    if(token == null ) {
                        sendMessage(resignCommand.getGameID(), new ErrorMessage("Error: unauthorized"), authToken);
                        return;
                    }


                    String userName = token.getUsername();
                    String gameID = resignCommand.getGameID();
                    ChessGame gameBoard = gameDAO.getGame(gameID).getGame();

                    if(gameBoard.getTeamTurn() == null) {
                        sendMessage(gameID, new ErrorMessage("Game over already bud"), authToken);
                        return;
                    }
                    if (gameBoard.getResign() != null){
                        sendMessage(gameID, new ErrorMessage("Error: " + gameBoard.getResign() + " already resigned"), authToken);
                        return;
                    }


                    gameBoard.setResign(token.getUsername());
                    GameData game = gameDAO.getGame(gameID);
                    gameDAO.updateGame(gameID, new GameData(game.getName(), gameID, game.getWhite(), game.getBlack(), gameBoard));

                    if (!Objects.equals(gameDAO.getGame(gameID).getWhite(), userName) && !Objects.equals(gameDAO.getGame(gameID).getBlack(), userName)) {
                        sendMessage(gameID, new ErrorMessage("You cannot Resign as an Observer."), authToken);
                        return;
                    }


                    Notification notification = new Notification(userName + " resigned.");
                    Notification notificationRoot = new Notification("You resigned the game.");

                    sendMessage(gameID, notificationRoot, authToken);
                    broadcastMessage(gameID, notification, authToken);


                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private void addPlayer(String gameID, String token, Session session) {
        Map<String, Session> gameConnections = sessionMap.computeIfAbsent(gameID, k -> new HashMap<>());
        gameConnections.put(token, session);
    }

    private void sendMessage(String gameID, ServerMessage message, String token) {
        Map<String, Session> gameConnections = sessionMap.get(gameID);
        Session targetSession = gameConnections.get(token);

        if(targetSession != null && targetSession.isOpen()) {
            try {
                targetSession.getRemote().sendString(new Gson().toJson(message));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String gameID, ServerMessage message, String exceptThisAuthToken) {
        Map<String, Session> gameConnections = sessionMap.get(gameID);
        for (Map.Entry<String, Session> entry : gameConnections.entrySet()) {
            String authToken = entry.getKey();
            if(!authToken.equals(exceptThisAuthToken)) {
                Session targetSession = entry.getValue();
                if(targetSession.isOpen()) {
                    try {
                        targetSession.getRemote().sendString(new Gson().toJson(message));
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void removeSessionFromGame(String gameID, String authToken) {
        Map<String, Session> gameConnections = sessionMap.get(gameID);
        if(gameConnections != null) {
            gameConnections.remove(authToken);
            if(gameConnections.isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
    }
    private void removeSession(Session session) {
        Set<Map.Entry<String, Map<String, Session>>> entrySet = sessionMap.entrySet();
        for(Map.Entry<String, Map<String, Session>> entry : entrySet) {
            Map<String, Session> gameConnections = entry.getValue();
            gameConnections.values().removeIf(s -> s.equals(session));
            if(gameConnections.isEmpty()) {
                entrySet.remove(entry);
            }
        }
    }
}
