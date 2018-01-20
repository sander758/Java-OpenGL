package nl.sander758.common.network;

public abstract class Packet {

    public enum PacketType {
        ENTITY_MOVE_PACKET (1),
        DISCONNECT_PACKET (2);

        private final int id;

        PacketType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PacketType getById(int id) {
            for (PacketType type : PacketType.values()) {
                if (type.getId() == id) return type;
            }
            return null;
        }
    }

    public Packet() {}

    public abstract void serialize(DataSerializer serializer);

    public abstract void deserialize(DataDeserializer deserializer);

    public abstract PacketType getId();
}
