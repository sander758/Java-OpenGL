package nl.sander758.gameserver.network.packetsOut;

import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.PacketOut;
import nl.sander758.common.network.PacketType;

public class ChatMessagePacketOut extends PacketOut {

    private String message;

    private int sender;

    public ChatMessagePacketOut(String message, int sender) {
        this.message = message;
        this.sender = sender;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeString(message);
        serializer.writeInt(sender);
    }

    @Override
    public PacketType getId() {
        return PacketType.SERVER_CHAT_PACKET;
    }
}
