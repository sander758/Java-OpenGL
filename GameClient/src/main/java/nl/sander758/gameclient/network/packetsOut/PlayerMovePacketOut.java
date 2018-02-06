package nl.sander758.gameclient.network.packetsOut;

import nl.sander758.common.network.*;
import org.joml.Vector3f;

public class PlayerMovePacketOut extends PacketOut {

    private Vector3f location;

    public PlayerMovePacketOut(Vector3f location) {
        this.location = location;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeVector3f(location);
    }
    @Override
    public PacketType getId() {
        return PacketType.PLAYER_MOVE_PACKET;
    }
}
