package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.Packet;
import nl.sander758.common.network.PacketListenerRegistry;
import nl.sander758.common.network.packets.DisconnectPacket;
import nl.sander758.common.network.packets.EntityMovePacket;

import java.io.*;

class SocketListener extends SocketRunnable {

    public SocketListener(SocketClient client) {
        super(client);
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
            Logger.error(e);
        }
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
                            client.trySend(new DisconnectPacket(false));
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
