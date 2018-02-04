package nl.sander758.gameclient.engine.player;

import nl.sander758.gameclient.engine.entitySystem.StaticEntityRegistry;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;

public class PlayerHandler {

    private static PlayablePlayer playablePlayer = null;

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
        System.out.println("updating server players");
        // TODO implement this
    }

}
