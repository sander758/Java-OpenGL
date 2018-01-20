package nl.sander758.gameserver.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.Packet;
import nl.sander758.common.network.packets.DisconnectPacket;
import nl.sander758.common.network.packets.EntityMovePacket;

import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private int clientId;
    private Socket socket;
    private SocketServer server;

    private DataInputStream input;
    private DataOutputStream output;

    private boolean isRunning = true;

    public ClientConnection(int clientId, Socket socket, SocketServer server) {
        this.clientId = clientId;
        this.socket = socket;
        this.server = server;

        try {
            InputStream inputStream = socket.getInputStream();
            input = new DataInputStream(inputStream);

            OutputStream outputStream = socket.getOutputStream();
            output = new DataOutputStream(outputStream);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            isRunning = false;
        }
    }

    public void trySend(Packet packet) {
        DataSerializer dataSerializer = new DataSerializer();
        Packet.PacketType packetType = packet.getId();
        dataSerializer.writeUnsignedByte(packetType.getId());

        packet.serialize(dataSerializer);
        byte[] data = dataSerializer.toByteArray();

        try {
            output.writeInt(data.length);
            output.write(data);
            output.flush();
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                int inputLength = input.readInt();
                byte[] input = new byte[inputLength];
                int readBytes = this.input.read(input);

                DataDeserializer deserializer = new DataDeserializer(input);
                byte packetId = deserializer.readUnsignedByte();

                Packet.PacketType type = Packet.PacketType.getById(packetId);

                if (type == null) {
                    Logger.error("Invalid packed received from client: " + packetId);
                    continue;
                }

                switch (type) {
                    case ENTITY_MOVE_PACKET:
                        EntityMovePacket movePacket = new EntityMovePacket();
                        movePacket.deserialize(deserializer);
                        Logger.debug("Entity move received: " + movePacket.getLocation());
                        break;
                    case DISCONNECT_PACKET:
                        Logger.debug("Disconnect packet received from client: " + clientId);
                        DisconnectPacket disconnectPacket = new DisconnectPacket();
                        disconnectPacket.deserialize(deserializer);
                        if (disconnectPacket.shouldPingBack()) {
                            trySend(new DisconnectPacket(false));
                        }
                        server.removeClient(clientId, "Disconnect requested from client");
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
