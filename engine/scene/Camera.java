package scene;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import entities.Terrain;
import utils.DisplayManager;

import java.util.List;

public class Camera {
    private final float speed = 5f;
    private final float mouseSensitivity = 0.2f;
    private float currentSpeed = speed;

    private Vector3f position;
    private float pitch = 45;
    private float yaw = 270;

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

    public void move(List<Terrain> terrains) {
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

        Terrain currentTerrain = null;

        for (Terrain terrain : terrains) {
            float pointAx = terrain.getPosition().x - terrain.getSizeOffset().x;
            float pointAz = terrain.getPosition().z - terrain.getSizeOffset().y;

            float pointBx = terrain.getPosition().x + terrain.getSizeOffset().x;
            float pointBz = terrain.getPosition().z + terrain.getSizeOffset().y;

            Vector2f highest = new Vector2f(pointAx, pointAz);
            Vector2f lowest = new Vector2f(pointBx, pointBz);

            if (pointBx > pointAx) {
                highest.x = pointBx;
            }
            if (pointBz > pointAz) {
                highest.y = pointBz;
            }

            if (pointAx < pointBx) {
                lowest.x = pointAx;
            }
            if (pointAz < pointBz) {
                lowest.y = pointAz;
            }

            if (position.x >= lowest.x
                    && position.x <= highest.x
                    && position.z >= lowest.y
                    && position.z <= highest.y) {

                currentTerrain = terrain;
                break;
            }
        }

        if (currentTerrain == null) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                position.y -= this.currentSpeed * time;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                position.y += this.currentSpeed * time;
            }
            return;
        }

        float height = currentTerrain.getHeight(position.x, position.z);

        float positionY = position.y;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            positionY -= this.currentSpeed * time;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            positionY += this.currentSpeed * time;
        }
        if (positionY < height) {
            positionY = height;
        }
        position.y = positionY;
    }
}
