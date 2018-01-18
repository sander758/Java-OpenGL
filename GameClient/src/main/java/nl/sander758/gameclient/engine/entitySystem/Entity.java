package nl.sander758.gameclient.engine.entitySystem;

import org.joml.Vector3f;

public abstract class Entity {
    private Vector3f location;

    public Entity(Vector3f location) {
        this.location = location;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }
}
