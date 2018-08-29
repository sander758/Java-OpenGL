package nl.sander758.gameserver.chat;

import nl.sander758.gameserver.network.ClientConnection;
import nl.sander758.gameserver.network.SocketServer;
import nl.sander758.gameserver.network.packetsOut.ChatMessagePacketOut;

public class ServerChatManager {

    private static ServerChatManager manager;

    public static ServerChatManager getManager() {
        return manager;
    }

    public static void initializeManager(SocketServer server) {
        manager = new ServerChatManager(server);
    }

    private SocketServer server;

    public ServerChatManager(SocketServer server) {
        this.server = server;
    }

    public void onClientMessage(int clientId, String message) {
        System.out.println("New client message from: " + clientId + " message: " + message);

        ChatMessagePacketOut packetOut = new ChatMessagePacketOut(message, clientId);

        for (Integer clientConnectionId : server.getConnections().keySet()) {
            ClientConnection client = server.getConnections().get(clientConnectionId);
            client.trySend(packetOut);
        }
    }
}
