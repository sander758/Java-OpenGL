package nl.sander758.gameserver.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.gameserver.player.PlayerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SocketServer implements Runnable {

    private int port;

    private int clientCount = 0;
    private HashMap<Integer, ClientConnection> connections = new HashMap<>();

    private boolean isRunning = true;

    public SocketServer(int port) {
        this.port = port;

        SocketUpdater socketUpdater = new SocketUpdater(this);
        new Thread(socketUpdater).start();
    }

    @Override
    public void run() {
        try {
            Logger.info("Starting server at port: " + port);
            ServerSocket server = new ServerSocket(port);

            while (isRunning) {
                Logger.info("Waiting for next socket");
                addClient(server.accept());
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public HashMap<Integer, ClientConnection> getConnections() {
        return connections;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void addClient(Socket socket) {
        try {
            int clientId = clientCount;
            clientCount++;

            Logger.info("Adding new socket with id: " + clientId);
            ClientConnection client = new ClientConnection(clientId, socket, this);
            client.start();
            connections.put(clientId, client);
        } catch (IOException e) {
            Logger.error(e);
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
        connections.remove(clientId);
        PlayerHandler.getPlayerHandler().removePlayer(clientId);
    }
}
