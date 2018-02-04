package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.Model;

public abstract class ServerEntity extends Entity {
    private final int serverId;

    public ServerEntity(Model model) {
        super(model);
        this.serverId = 0;
    }
}
