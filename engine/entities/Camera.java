package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import utils.DisplayManager;

public class Camera {
    private final float speed = 5f;
    private final float mouseSensitivity = 0.2f;
    private float currentSpeed = speed;

    private Vector3f position;
    private float pitch = 45;
    private float yaw = 0;

    public Camera(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void move() {
        // middle mouse button
        if (Mouse.isButtonDown(2)) {
            pitch += -Mouse.getDY() * mouseSensitivity;
            yaw += Mouse.getDX() * mouseSensitivity;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            currentSpeed = speed * 5;
        } else {
            currentSpeed = speed;
        }

        if (pitch > 90.0f) {
            pitch = 90.0f;
        }
        if (pitch < -90.0f) {
            pitch = -90.0f;
        }

        float newX = (float) (Math.sin(Math.toRadians(yaw)));
        float newZ = (float) (Math.cos(Math.toRadians(yaw)));
        float time = DisplayManager.getFrameTime();
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.x += newX * this.currentSpeed * time;
            position.z -= newZ * this.currentSpeed * time;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.x -= newX * this.currentSpeed * time;
            position.z += newZ * this.currentSpeed * time;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += newZ * this.currentSpeed * time;
            position.z += newX * this.currentSpeed * time;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= newZ * this.currentSpeed * time;
            position.z -= newX * this.currentSpeed * time;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            position.y -= this.currentSpeed * time;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            position.y += this.currentSpeed * time;
        }
    }
}
