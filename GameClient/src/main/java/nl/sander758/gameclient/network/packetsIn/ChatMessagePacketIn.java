package nl.sander758.gameclient.network.packetsIn;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;

import static nl.sander758.common.network.PacketType.SERVER_CHAT_PACKET;

public class ChatMessagePacketIn extends PacketIn {

    private String message;

    private int sender;

    @Override
    public void deserialize(DataDeserializer deserializer) {
        message = deserializer.readString();
        sender = deserializer.readInt();
    }

    @Override
    public PacketType getId() {
        return SERVER_CHAT_PACKET;
    }

    public String getMessage() {
        return message;
    }

    public int getSender() {
        return sender;
    }
}
