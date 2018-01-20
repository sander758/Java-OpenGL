package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.Packet;
import nl.sander758.common.network.PacketListenerRegistry;
import nl.sander758.common.network.packets.DisconnectPacket;
import nl.sander758.common.network.packets.EntityMovePacket;

import java.io.*;
import java.net.Socket;

class SocketConnection implements Runnable {

    private String address;
    private int port;

    private boolean isRunning = true;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private Thread listenThread;

    public SocketConnection(String address, int port) throws IOException {
        this.address = address;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(address, port);

        InputStream inputStream = socket.getInputStream();
        input = new DataInputStream(inputStream);

        OutputStream outputStream = socket.getOutputStream();
        output = new DataOutputStream(outputStream);

        listenThread = new Thread(this);
        listenThread.start();
    }

    public void trySend(Packet packet) {
        DataSerializer serializer = new DataSerializer();
        Packet.PacketType packetType = packet.getId();
        serializer.writeUnsignedByte(packetType.getId());

        packet.serialize(serializer);
        byte[] data = serializer.toByteArray();

        try {
            output.writeInt(data.length);
            output.write(data);
            output.flush();
        } catch (IOException e) {
            Logger.error(e, e.getMessage());
        }
    }

    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    private void close() {
        try {
            isRunning = false;
            if (!isAlive()) {
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
                        EntityMovePacket entityMovePacket = new EntityMovePacket();
                        entityMovePacket.deserialize(deserializer);
                        PacketListenerRegistry.callListeners(type, entityMovePacket);
                        break;
                    case DISCONNECT_PACKET:
                        Logger.debug("Disconnect packet received");
                        DisconnectPacket disconnectPacket = new DisconnectPacket();
                        disconnectPacket.deserialize(deserializer);
                        if (disconnectPacket.shouldPingBack()) {
                            SocketClient.trySend(new DisconnectPacket(false));
                        }
                        close();
                        break;
                }
            }
        } catch (IOException e) {
            Logger.error(e, e.getMessage());
        }
    }
}
