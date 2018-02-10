package nl.sander758.common.network.packetsOut;

import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.PacketOut;
import nl.sander758.common.network.PacketType;

public class DisconnectPacketOut extends PacketOut {

    private boolean shouldPingBack;

    public DisconnectPacketOut(boolean shouldPingBack) {
        this.shouldPingBack = shouldPingBack;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeBoolean(shouldPingBack);
    }

    @Override
    public PacketType getId() {
        return PacketType.DISCONNECT_PACKET;
    }
}
