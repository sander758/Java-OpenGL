package nl.sander758.common.network.packetsIn;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class DisconnectPacketIn extends PacketIn {

    private boolean shouldPingBack;

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
