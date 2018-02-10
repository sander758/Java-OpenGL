package nl.sander758.common.network.packetsOut;

import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.PacketOut;
import nl.sander758.common.network.PacketType;

public class PongPacketOut extends PacketOut {

    private int data;

    public PongPacketOut(int data) {
        this.data = data;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeInt(data);
    }

    @Override
    public PacketType getId() {
        return PacketType.PONG_PACKET;
    }
}
