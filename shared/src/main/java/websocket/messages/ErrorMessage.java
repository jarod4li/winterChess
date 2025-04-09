package websocket.messages;

import websocket.messages.ServerMessage;

public class ErrorMessage extends ServerMessage {
    public String errorMessage;

    public ErrorMessage(String message){
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }
}
