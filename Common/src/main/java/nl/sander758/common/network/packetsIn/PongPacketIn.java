package nl.sander758.common.network.packetsIn;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class PongPacketIn extends PacketIn {

    private int data;

    @Override
    public void deserialize(DataDeserializer deserializer) {
        data = deserializer.readInt();
    }

    @Override
    public PacketType getId() {
        return PacketType.PONG_PACKET;
    }

    public int getData() {
        return data;
    }
}
