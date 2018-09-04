package nl.sander758.gameclient.engine.guiSystem.chat;

public class ChatMessage {
    private int sender;

    private String message;

    public ChatMessage(int sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
