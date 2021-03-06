package nl.sander758.common.network;

public enum PacketType {
    REGISTER_PACKET(1),
    ACCEPT_REGISTER(2),
    DISCONNECT_PACKET(3),
    PLAYER_MOVE_PACKET(4),
    PLAYERS_LOCATION_PACKET(5),
    REMOVE_PLAYER(6),
    PING_PACKET(7),
    PONG_PACKET(8),
    CLIENT_CHAT_PACKET(9),
    SERVER_CHAT_PACKET(10);

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
