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

    public void removePlayer(int clientId) {
        players.remove(clientId);
    }

    public Player getPlayer(int clientId) {
        return players.get(clientId);
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }
}
