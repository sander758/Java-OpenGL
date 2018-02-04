package nl.sander758.gameserver.network.packets.in;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class ClientRegisterPacketIn extends PacketIn {

    @Override
    public void deserialize(DataDeserializer deserializer) {

    }

    @Override
    public PacketType getId() {
        return PacketType.REGISTER_PACKET;
    }
}
