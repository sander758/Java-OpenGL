package nl.sander758.gameclient.engine.player;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FirstPersonCamera implements Camera {

    private Matrix4f viewMatrix = new Matrix4f();

    @Override
    public void updateViewMatrix(Vector3f location, float yaw, float pitch) {
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        viewMatrix.translate(new Vector3f(-location.x, -location.y, -location.z));
    }

    @Override
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
