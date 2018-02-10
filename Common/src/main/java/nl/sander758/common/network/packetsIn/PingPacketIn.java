package nl.sander758.common.network.packetsIn;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class PingPacketIn extends PacketIn {

    private int data;

    @Override
    public void deserialize(DataDeserializer deserializer) {
        data = deserializer.readInt();
    }

    @Override
    public PacketType getId() {
        return PacketType.PING_PACKET;
    }

    public int getData() {
        return data;
    }
}
