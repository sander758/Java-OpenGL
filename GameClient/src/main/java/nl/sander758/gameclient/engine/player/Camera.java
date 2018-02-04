package nl.sander758.gameclient.engine.player;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface Camera {
    void updateViewMatrix(Vector3f location, float yaw, float pitch);

    Matrix4f getViewMatrix();
}
