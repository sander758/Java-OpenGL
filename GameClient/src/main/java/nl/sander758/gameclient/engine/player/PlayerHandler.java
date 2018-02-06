package nl.sander758.gameclient.engine.player;

import nl.sander758.common.logger.Logger;
import nl.sander758.gameclient.engine.entitySystem.StaticEntityRegistry;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PlayerHandler {

    private static PlayablePlayer playablePlayer = null;

    private static HashMap<Integer, ServerPlayerEntity> serverPlayers = new HashMap<>();

    public static void createPlayablePlayer(int clientId) throws ModelNotFoundException {
        playablePlayer = new PlayablePlayer(clientId);
        StaticEntityRegistry.addEntity(playablePlayer);
    }

    public static PlayablePlayer getPlayablePlayer() throws PlayerNotFoundException {
        if (playablePlayer == null) {
            throw new PlayerNotFoundException("No playable player found or initialized");
        }
        return playablePlayer;
    }

    public static void updateServerPlayers(HashMap<Integer, Vector3f> players) {
        try {
            long timestamp = System.currentTimeMillis();

            for (Integer clientId : players.keySet()) {
                if (playablePlayer == null || clientId == playablePlayer.getClientId()) {
                    continue;
                }

                if (!serverPlayers.containsKey(clientId)) {
                    ServerPlayerEntity playerEntity = new ServerPlayerEntity(clientId);
                    serverPlayers.put(clientId, playerEntity);
                }
                serverPlayers.get(clientId).updateState(timestamp, players.get(clientId));
            }
        } catch (ModelNotFoundException e) {
            Logger.error(e);
        }
    }

    public static Collection<ServerPlayerEntity> getServerPlayerEntities() {
        return serverPlayers.values();
    }

}
