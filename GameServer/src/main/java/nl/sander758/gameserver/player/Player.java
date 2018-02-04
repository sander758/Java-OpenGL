package nl.sander758.gameserver.player;

import org.joml.Vector3f;

public class Player {

    private final int id;

    private Vector3f location;

    public Player(int id) {
        this.id = id;
        this.location = new Vector3f(0, 0, 0);
    }

    public int getId() {
        return id;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }
}
