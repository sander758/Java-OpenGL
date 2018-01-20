package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.Packet;
import nl.sander758.common.network.packets.DisconnectPacket;

import java.io.IOException;

public class SocketClient {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 9000;

    private static SocketConnection connection;

    public static void connect() {
        try {
            if (connection != null) {
                Logger.error("Connection instance already exists");
                return;
            }
            connection = new SocketConnection(SERVER_ADDRESS, PORT);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void trySend(Packet packet) {
        if (connection == null) {
            Logger.debug("No active socket connection");
            return;
        }
        if (!connection.isAlive()) {
            Logger.error("Socket connection is not alive anymore");
            return;
        }
        connection.trySend(packet);
    }

    public static void disconnect() {
        trySend(new DisconnectPacket(true));
    }
}
