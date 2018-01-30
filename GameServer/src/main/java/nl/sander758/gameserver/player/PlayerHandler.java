package nl.sander758.gameserver.player;

import java.util.Collection;
import java.util.HashMap;

public class PlayerHandler {

    private HashMap<Integer, Player> players = new HashMap<>();

    private static PlayerHandler handler = new PlayerHandler();

    private PlayerHandler() {}

    public static PlayerHandler getPlayerHandler() {
        return handler;
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void removePlayer(int playerId) {
        players.remove(playerId);
    }

    public Player getPlayer(int playerId) {
        return players.get(playerId);
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }
}
