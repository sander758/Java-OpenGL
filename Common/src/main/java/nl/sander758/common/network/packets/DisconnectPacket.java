package nl.sander758.common.network.packets;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.Packet;

public class DisconnectPacket extends Packet {

    private boolean shouldPingBack;

    public DisconnectPacket() {
        super();
    }

    public DisconnectPacket(boolean shouldPingBack) {
        this.shouldPingBack = shouldPingBack;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeBoolean(shouldPingBack);
    }

    @Override
    public void deserialize(DataDeserializer deserializer) {
        shouldPingBack = deserializer.readBoolean();
    }

    @Override
    public PacketType getId() {
        return PacketType.DISCONNECT_PACKET;
    }

    public boolean shouldPingBack() {
        return shouldPingBack;
    }
}
