package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.Model;

public abstract class ServerEntity extends StaticEntity {
    private final int clientId;

    public ServerEntity(Model model, int clientId) {
        super(model);
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }
}
