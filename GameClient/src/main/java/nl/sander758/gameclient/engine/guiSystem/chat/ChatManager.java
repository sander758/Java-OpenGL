package nl.sander758.gameclient.engine.guiSystem.chat;

import com.google.common.collect.Lists;
import nl.sander758.gameclient.engine.display.WindowManager;
import nl.sander758.gameclient.engine.guiSystem.texts.FontStyle;
import nl.sander758.gameclient.engine.guiSystem.texts.GuiText;
import nl.sander758.gameclient.engine.guiSystem.texts.TextFactory;
import nl.sander758.gameclient.engine.input.InputManager;
import nl.sander758.gameclient.engine.input.KeyboardInputListener;
import nl.sander758.gameclient.network.SocketClient;
import nl.sander758.gameclient.network.packetsOut.ClientChatMessagePacketOut;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class ChatManager implements KeyboardInputListener  {

    private Vector2f location;

    private float width;

    private float height;

    private float fontSize = 0.25f;

    private boolean hasChatOpen = false;

    private String currentChatMessage = "";

    private FontStyle fontStyle;

    private List<ChatMessage> unprocessedMessages = new ArrayList<>();

    private List<GuiText> messages = new ArrayList<>();

    private GuiText unsentMessage;

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

        TextFactory textFactory = TextFactory.getTextFactory();
        fontStyle = textFactory.getFontStyle("verdana");

        System.out.println("Chatmanager initialized");
        InputManager.registerKeyboardInputListener(this);

        unsentMessage = new GuiText("", fontStyle, fontSize, width, new Vector2f(location.x, 0), new Vector3f(0, 255, 0));
    }

    public void onServerMessage(ChatMessage chatMessage) {
        System.out.println("Received server message: " + chatMessage.getMessage() + " from: " + chatMessage.getSender());
        unprocessedMessages.add(chatMessage);
    }

    /**
     * Gets called every render loop to create new chat messages, because VAO and VBO's need to be created on the OpenGL thread.
     */
    public void handleMessages() {
        if (unprocessedMessages.size() > 0) {
            for (ChatMessage message : unprocessedMessages) {
                GuiText text = new GuiText(message.getSender() + ": " + message.getMessage(), fontStyle, fontSize, width, new Vector2f(0, 0), new Vector3f(255, 255, 0));
                messages.add(text);
            }
            unprocessedMessages.clear();


            float windowHeight = (float) WindowManager.getHeight() / 2;
            float offset = (fontStyle.getLineHeight()) * fontSize / windowHeight;

            int index = 0;
            for (GuiText text : Lists.reverse(messages)) {
                float messagesHeight = getSummedLineHeight(messages.subList(0, index));
                text.setPosition(new Vector2f(location.x, location.y - (height * 2) + offset + messagesHeight));
                index++;
            }
        }
    }

    public boolean hasChatOpen() {
        return hasChatOpen;
    }

    public List<GuiText> getMessages() {
        return messages;
    }

    public GuiText getUnsentMessage() {
        return unsentMessage;
    }

    public FontStyle getFontStyle() {
        return fontStyle;
    }

    private float getSummedLineHeight(List<GuiText> messages) {
        float height = 0f;
        height += unsentMessage.getHeight();
        for (GuiText guiText : messages) {
            height += guiText.getHeight();
            System.out.println(guiText.getHeight());
        }
        return height / (float) WindowManager.getHeight() * 2;
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
                unsentMessage.setText(currentChatMessage);
            } else {
                hasChatOpen = true;
            }
        } else if (action == GLFW_PRESS && hasChatOpen && key >= 32 && key <= 96) {
            currentChatMessage += (char) key;
            unsentMessage.setText(currentChatMessage);

            float windowHeight = (float) WindowManager.getHeight() / 2;
//            float offset = (fontStyle.getLineHeight()) * fontSize / windowHeight;
            float messagesHeight = unsentMessage.getHeight() / windowHeight;
            unsentMessage.setPosition(new Vector2f(location.x, location.y - (height * 2) + messagesHeight));
        }
    }
}
