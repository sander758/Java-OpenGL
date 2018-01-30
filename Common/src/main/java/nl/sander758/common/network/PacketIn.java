package nl.sander758.common.network;

public abstract class PacketIn {

    public abstract void deserialize(DataDeserializer deserializer);

    public abstract PacketType getId();
}
