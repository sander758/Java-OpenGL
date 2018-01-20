package nl.sander758.gameserver.network;

import nl.sander758.common.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SocketServer implements Runnable {

    private int port;

    private int clientCount = 0;
    private HashMap<Integer, ClientConnection> connections = new HashMap<>();

    public SocketServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Logger.info("Starting server at port: " + port);
            ServerSocket server = new ServerSocket(port);

            while (true) {
                Logger.info("Waiting for next client");
                addClient(server.accept());
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeClient(int clientId, String reason) {
        if (!connections.containsKey(clientId)) {
            Logger.error("Unknown clientId to remove");
            return;
        }
        Logger.debug("Client: " + clientId + " disconnected with reason: " + reason);
        ClientConnection connection = connections.get(clientId);
        connection.close();
    }

    private void addClient(Socket socket) {
        int clientId = clientCount;
        clientCount++;

        Logger.info("Adding new client with id: " + clientId);
        ClientConnection client = new ClientConnection(clientId, socket, this);
        new Thread(client).start();
        connections.put(clientId, client);
    }
}
