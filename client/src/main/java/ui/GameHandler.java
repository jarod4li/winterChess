package ui;


import websocket.messages.*;

public interface GameHandler {
    public void updateGame (LoadGame game);
    public void printNotification (Notification message);
}
