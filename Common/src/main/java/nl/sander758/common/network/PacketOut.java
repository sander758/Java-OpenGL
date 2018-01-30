package nl.sander758.common.network;

public abstract class PacketOut {
    public abstract void serialize(DataSerializer serializer);

    public abstract PacketType getId();
}
