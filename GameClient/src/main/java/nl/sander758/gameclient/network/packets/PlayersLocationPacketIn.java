package nl.sander758.gameclient.network.packets;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class PlayersLocationPacketIn extends PacketIn {



    @Override
    public void deserialize(DataDeserializer deserializer) {

    }

    @Override
    public PacketType getId() {
        return PacketType.PLAYERS_LOCATION_PACKET;
    }
}
