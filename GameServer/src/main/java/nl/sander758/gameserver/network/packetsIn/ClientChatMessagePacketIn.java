package nl.sander758.gameserver.network.packetsIn;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

public class ClientChatMessagePacketIn extends PacketIn {

    private String message;

    @Override
    public void deserialize(DataDeserializer deserializer) {
        message = deserializer.readString();
    }

    @Override
    public PacketType getId() {
        return PacketType.CLIENT_CHAT_PACKET;
    }

    public String getMessage() {
        return message;
    }
}
