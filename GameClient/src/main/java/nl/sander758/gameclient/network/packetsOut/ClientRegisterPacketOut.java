package nl.sander758.gameclient.network.packetsOut;

import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.PacketOut;
import nl.sander758.common.network.PacketType;

public class ClientRegisterPacketOut extends PacketOut {

    @Override
    public void serialize(DataSerializer serializer) {

    }

    @Override
    public PacketType getId() {
        return PacketType.REGISTER_PACKET;
    }
}
