package nl.sander758.gameserver.network.packets;

import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.PacketOut;
import nl.sander758.common.network.PacketType;
import nl.sander758.gameserver.player.Player;

import java.util.Collection;

public class PlayersLocationPacketOut extends PacketOut {

    private Collection<Player> players;

    public PlayersLocationPacketOut(Collection<Player> players) {
        this.players = players;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeInt(players.size()); // amount of players

        for (Player player : players) {
            serializer.writeInt(player.getId());
            serializer.writeVector3f(player.getPosition());
        }
    }

    @Override
    public PacketType getId() {
        return PacketType.PLAYERS_LOCATION_PACKET;
    }
}
