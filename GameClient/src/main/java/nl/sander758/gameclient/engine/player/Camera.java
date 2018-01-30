package nl.sander758.gameclient.engine.player;

import org.joml.Vector3f;

public interface Camera {

    public Vector3f getLocation();

    public Vector3f getNewLocation();

    public float getPitch();

    public void invertPitch();

    public float getYaw();

    public void update();
}
