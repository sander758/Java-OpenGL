package nl.sander758.gameclient.network.packets.in;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class AcceptRegisterPacketIn extends PacketIn {

    private int clientId;

    @Override
    public void deserialize(DataDeserializer deserializer) {
        clientId = deserializer.readInt();
    }

    @Override
    public PacketType getId() {
        return PacketType.ACCEPT_REGISTER;
    }

    public int getClientId() {
        return clientId;
    }
}
