package nl.sander758.common.network.packets;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.Packet;
import org.joml.Vector3f;

public class EntityMovePacket extends Packet {

    private Vector3f location;

    public EntityMovePacket() {
        super();
    }

    public EntityMovePacket(Vector3f location) {
        this.location = location;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        if (location == null) {
            return;
        }
        serializer.writeFloat(location.x);
        serializer.writeFloat(location.y);
        serializer.writeFloat(location.z);
    }

    @Override
    public void deserialize(DataDeserializer deserializer) {
        float x = deserializer.readFloat();
        float y = deserializer.readFloat();
        float z = deserializer.readFloat();
        location = new Vector3f(x, y, z);
    }

    @Override
    public PacketType getId() {
        return PacketType.ENTITY_MOVE_PACKET;
    }

    public Vector3f getLocation() {
        return location;
    }
}
