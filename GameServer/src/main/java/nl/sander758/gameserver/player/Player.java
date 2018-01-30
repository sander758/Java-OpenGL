package nl.sander758.gameserver.player;

import org.joml.Vector3f;

public class Player {

    private final int id;

    private Vector3f position;

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
