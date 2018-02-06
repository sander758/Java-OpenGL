package nl.sander758.gameclient.network.packetsIn;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class RemovePlayerPacketIn extends PacketIn {

    private int clientId;

    @Override
    public void deserialize(DataDeserializer deserializer) {
        clientId = deserializer.readInt();
    }

    @Override
    public PacketType getId() {
        return PacketType.REMOVE_PLAYER;
    }

    public int getClientId() {
        return clientId;
    }
}
