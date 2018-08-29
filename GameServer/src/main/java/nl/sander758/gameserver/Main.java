package nl.sander758.gameserver;

import nl.sander758.gameserver.chat.ServerChatManager;
import nl.sander758.gameserver.network.SocketServer;

public class Main {

    private static final int PORT = 9000;

    public static void main(String args[]) {
        SocketServer server = new SocketServer(PORT);
        ServerChatManager.initializeManager(server);
        new Thread(server).start();
    }
}
