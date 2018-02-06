package nl.sander758.gameclient.network.packetsIn;

import nl.sander758.common.network.DataDeserializer;
import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketType;
import org.joml.Vector3f;

import java.util.HashMap;

public class PlayersLocationPacketIn extends PacketIn {

    private HashMap<Integer, Vector3f> players = new HashMap<>();

    @Override
    public void deserialize(DataDeserializer deserializer) {
        int playerSize = deserializer.readInt();

        for (int i = 0; i < playerSize; i++) {
            int clientId = deserializer.readInt();
            Vector3f location = deserializer.readVector3f();
            players.put(clientId, location);
        }
    }

    @Override
    public PacketType getId() {
        return PacketType.PLAYERS_LOCATION_PACKET;
    }

    public HashMap<Integer, Vector3f> getPlayersLocations() {
        return players;
    }
}
