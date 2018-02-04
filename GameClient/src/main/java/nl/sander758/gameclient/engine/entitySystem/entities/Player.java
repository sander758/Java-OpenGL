package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.Model;

public abstract class Player extends ServerEntity implements Nameable {

    private String name = "";

    public Player(Model model) {
        super(model);
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
