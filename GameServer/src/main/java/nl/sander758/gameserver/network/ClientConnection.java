package nl.sander758.gameserver.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.*;
import nl.sander758.common.network.packets.DisconnectPacketIn;
import nl.sander758.common.network.packets.DisconnectPacketOut;
import nl.sander758.gameserver.player.Player;
import nl.sander758.gameserver.player.PlayerHandler;
import nl.sander758.gameserver.network.packets.PlayerMovePacketIn;

import java.io.*;
import java.net.Socket;

public class ClientConnection extends SocketRunnable {

    private int clientId;
    private SocketServer server;

    private boolean isRunning = true;

    private Player player;

    public ClientConnection(int clientId, Socket socket, SocketServer server) {
        super(socket);
        this.clientId = clientId;
        this.server = server;

        player = new Player(clientId);
        PlayerHandler.getPlayerHandler().addPlayer(player);
    }

    @Override
    public void run() {
        try {
            while (server.isRunning() && isRunning) {
                int inputLength = input.readInt();
                byte[] inputData = new byte[inputLength];
                int readBytes = input.read(inputData);

                DataDeserializer deserializer = new DataDeserializer(inputData);
                byte packetId = deserializer.readUnsignedByte();

                PacketType type = PacketType.getById(packetId);

                if (type == null) {
                    Logger.error("Invalid packed received from socket: " + packetId);
                    continue;
                }

                switch (type) {
                    case PLAYER_MOVE_PACKET:
                        PlayerMovePacketIn playerMovePacketIn = new PlayerMovePacketIn();
                        playerMovePacketIn.deserialize(deserializer);
                        player.setPosition(playerMovePacketIn.getLocation());
                        break;
                    case DISCONNECT_PACKET:
                        Logger.debug("Disconnect packet received from socket: " + clientId);
                        DisconnectPacketIn disconnectPacket = new DisconnectPacketIn();
                        disconnectPacket.deserialize(deserializer);
                        if (disconnectPacket.shouldPingBack()) {
                            trySend(new DisconnectPacketOut(false));
                        }
                        server.removeClient(clientId, "Disconnect requested from socket");
                        break;
                }
            }
        } catch (IOException e) {
            server.removeClient(clientId, "Unexpected shutdown: " + e.getMessage());
            Logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isAlive() {
        return !socket.isClosed();
    }

    public void close() {
        try {
            isRunning = false;
            if (isAlive()) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
