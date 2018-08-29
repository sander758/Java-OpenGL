package nl.sander758.gameclient.network.packetsOut;

import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.PacketOut;
import nl.sander758.common.network.PacketType;

public class ClientChatMessagePacketOut extends PacketOut {

    private String message;

    public ClientChatMessagePacketOut(String message) {
        this.message = message;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeString(this.message);
    }

    @Override
    public PacketType getId() {
        return PacketType.CLIENT_CHAT_PACKET;
    }
}
