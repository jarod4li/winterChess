package ui;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import websocket.messages.*;
import websocket.commands.*;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    GameHandler gameHandler;
    public Session session;

    public WebSocketFacade(String url, GameHandler gameHandler) {
        try {
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/connect");
            this.gameHandler = gameHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        Notification notification = new Gson().fromJson(message, Notification.class);
                        gameHandler.printNotification(notification);
                    }
                    if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                        LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                        gameHandler.updateGame(loadGame);
                    }
                }
            });
        } catch (IOException | URISyntaxException | DeploymentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig){
    }
    private void helperSendMessage(UserGameCommand command){
        try{
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void joinGame(String token, String gameID, ChessGame.TeamColor teamColor){
        Join join = new Join(token, gameID, teamColor);
        helperSendMessage(join);
    }
    public void leaveGame(String token, String gameID){
        Leave leave = new Leave(token, gameID);
        helperSendMessage(leave);
    }
    public void makeMove(String token, String gameID, ChessMove move){
        MakeMove moveCom = new MakeMove(token, gameID, move);
        helperSendMessage(moveCom);
    }
    public void resign(String token, String gameID){
        Resign resignCom = new Resign(token, gameID);
        helperSendMessage(resignCom);
    }
}
