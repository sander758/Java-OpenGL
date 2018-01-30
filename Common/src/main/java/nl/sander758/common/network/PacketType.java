package nl.sander758.common.network;

public enum PacketType {
    PLAYER_MOVE_PACKET(1),
    DISCONNECT_PACKET(2),
    PLAYERS_LOCATION_PACKET(3);

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
