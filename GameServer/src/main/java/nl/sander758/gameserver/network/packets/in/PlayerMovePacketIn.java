package nl.sander758.gameserver.network.packets.in;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;
import org.joml.Vector3f;

public class PlayerMovePacketIn extends PacketIn {

    private Vector3f location;

    @Override
    public void deserialize(DataDeserializer deserializer) {
        location = deserializer.readVector3f();
    }

    @Override
    public PacketType getId() {
        return PacketType.PLAYER_MOVE_PACKET;
    }

    public Vector3f getLocation() {
        return location;
    }
}
