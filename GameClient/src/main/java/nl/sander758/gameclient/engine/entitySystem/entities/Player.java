package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import nl.sander758.gameclient.engine.loader.ModelRegistry;

public abstract class Player extends ServerEntity implements Nameable {

    private String name = "";

    public Player(int clientId) throws ModelNotFoundException {
        super(ModelRegistry.getModel("player"), clientId);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
