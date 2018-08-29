package nl.sander758.gameclient.engine.guiSystem.chat;

import nl.sander758.gameclient.engine.input.InputManager;
import nl.sander758.gameclient.engine.input.KeyboardInputListener;
import nl.sander758.gameclient.network.SocketClient;
import nl.sander758.gameclient.network.packetsIn.ChatMessagePacketIn;
import nl.sander758.gameclient.network.packetsOut.ClientChatMessagePacketOut;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class ChatManager implements KeyboardInputListener  {

    private Vector2f location;

    private float width;

    private float height;

    private boolean hasChatOpen = false;

    private String currentChatMessage = "";

    private static ChatManager manager;

    public static ChatManager getManager() {
        return manager;
    }

    public static void initialize() {
        manager = new ChatManager(new Vector2f(-1, 0), 0.5f, 0.5f);
    }

    /**
     * Creates a new chat manager with location and size.
     *
     * @param location The top left corner of the chat window
     * @param width the width in screen space, so 1 is the entire window width.
     * @param height the height in screen space, so 1 is the entire window height.
     */
    public ChatManager(Vector2f location, float width, float height) {
        this.location = location;
        this.width = width;
        this.height = height;

        System.out.println("Chatmanager initialized");
        InputManager.registerKeyboardInputListener(this);
    }

    public void onServerMessage(ChatMessagePacketIn chatPacket) {
        System.out.println("Received server message: " + chatPacket.getMessage() + " from: " + chatPacket.getSender());
    }

    @Override
    public void keyboardInputCallback(int key, int action) {
        if (key == GLFW_KEY_ENTER && action == GLFW_PRESS) {
            System.out.println("Enter pressed " + action);
            if (hasChatOpen) {
                // Send chat message
                System.out.println("try sending chat message: " + currentChatMessage.toLowerCase());
                SocketClient.getClient().trySend(new ClientChatMessagePacketOut(currentChatMessage.toLowerCase()));
                System.out.println("after sending chat message");
                hasChatOpen = false;
                currentChatMessage = "";
            } else {
                hasChatOpen = true;
            }
        } else if (action == GLFW_PRESS && key >= 32 && key <= 96) {
            currentChatMessage += (char) key;
        }
    }
}
