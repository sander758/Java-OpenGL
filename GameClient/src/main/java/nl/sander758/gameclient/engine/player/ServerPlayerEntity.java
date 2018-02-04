package nl.sander758.gameclient.engine.player;

import nl.sander758.gameclient.engine.entitySystem.entities.Player;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;

public class ServerPlayerEntity extends Player {

    public ServerPlayerEntity(int clientId) throws ModelNotFoundException {
        super(clientId);
    }
}
