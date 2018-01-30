package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.*;
import nl.sander758.common.network.packets.DisconnectPacketIn;
import nl.sander758.common.network.packets.DisconnectPacketOut;

import java.io.*;

class SocketListener extends SocketRunnable {

    private SocketClient client;

    public SocketListener(SocketClient client) {
        super(client.getSocket());
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (client.isRunning()) {
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
                    case DISCONNECT_PACKET:
                        Logger.debug("Disconnect packet received");
                        DisconnectPacketIn disconnectPacket = new DisconnectPacketIn();
                        disconnectPacket.deserialize(deserializer);
                        if (disconnectPacket.shouldPingBack()) {
                            trySend(new DisconnectPacketOut(false));
                        }
                        client.close();
                        break;
                }
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
