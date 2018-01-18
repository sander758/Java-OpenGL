package nl.sander758.gameclient.engine.display;

import nl.sander758.gameclient.engine.input.InputManager;
import nl.sander758.gameclient.engine.input.KeyboardInputListener;
import nl.sander758.gameclient.engine.input.MouseInputListener;
import nl.sander758.gameclient.engine.terrainSystem.Terrain;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Camera implements MouseInputListener, KeyboardInputListener {

    private static int
            KEY_UP = 0,
            KEY_DOWN = 1;


    private float speed = 0.2f;
    private float mouseSensitivity = 0.2f;

    private Vector3f position;
    private float pitch = 0;
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

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    private int W_KEY_STATE = 0;
    private int S_KEY_STATE = 0;
    private int D_KEY_STATE = 0;
    private int A_KEY_STATE = 0;
    private int LEFT_SHIT_STATE = 0;
    private int SPACE_STATE = 0;

    public void move(List<Terrain> terrains) {
        float newX = (float) (Math.sin(Math.toRadians(yaw)));
        float newZ = (float) (Math.cos(Math.toRadians(yaw)));

        if (W_KEY_STATE == KEY_DOWN) {
            position.x += newX * speed;
            position.z -= newZ * speed;
        }
        if (S_KEY_STATE == KEY_DOWN) {
            position.x -= newX * speed;
            position.z += newZ * speed;
        }
        if (D_KEY_STATE == KEY_DOWN) {
            position.x += newZ * speed;
            position.z += newX * speed;
        }
        if (A_KEY_STATE == KEY_DOWN) {
            position.x -= newZ * speed;
            position.z -= newX * speed;
        }

        if (LEFT_SHIT_STATE == KEY_DOWN) {
            position.y -= speed;
        }
        if (SPACE_STATE == KEY_DOWN) {
            position.y += speed;
        }

//        Terrain currentTerrain = null;
//
//        for (Terrain terrain : terrains) {
//            float pointAx = terrain.getLocation().x - terrain.getSizeOffset().x;
//            float pointAz = terrain.getLocation().z - terrain.getSizeOffset().y;
//
//            float pointBx = terrain.getLocation().x + terrain.getSizeOffset().x;
//            float pointBz = terrain.getLocation().z + terrain.getSizeOffset().y;
//
//            Vector2f highest = new Vector2f(pointAx, pointAz);
//            Vector2f lowest = new Vector2f(pointBx, pointBz);
//
//            if (pointBx > pointAx) {
//                highest.x = pointBx;
//            }
//            if (pointBz > pointAz) {
//                highest.y = pointBz;
//            }
//
//            if (pointAx < pointBx) {
//                lowest.x = pointAx;
//            }
//            if (pointAz < pointBz) {
//                lowest.y = pointAz;
//            }
//
//            if (position.x >= lowest.x
//                    && position.x <= highest.x
//                    && position.z >= lowest.y
//                    && position.z <= highest.y) {
//
//                currentTerrain = terrain;
//                break;
//            }
//        }
//
//        if (currentTerrain == null) {
//            if (LEFT_SHIT_STATE == KEY_DOWN) {
//                position.y -= speed;
//            }
//            if (SPACE_STATE == KEY_DOWN) {
//                position.y += speed;
//            }
//            return;
//        }
//
//        float height = currentTerrain.getHeight(position.x, position.z);

//        float positionY = position.y;
//        if (LEFT_SHIT_STATE == KEY_DOWN) {
//            positionY -= speed;
//        }
//        if (SPACE_STATE == KEY_DOWN) {
//            positionY += speed;
//        }
//        if (positionY < height) {
//            positionY = height;
//        }
//        position.y = positionY;
    }

    @Override
    public void mouseInputCallback(double offsetX, double offsetY) {
        int state = InputManager.getMouseButtonState(GLFW_MOUSE_BUTTON_MIDDLE);
        if (state != GLFW_PRESS) {
            return;
        }

        pitch += -offsetY * mouseSensitivity;
        yaw += offsetX * mouseSensitivity;

        if (pitch > 90.0f) {
            pitch = 90.0f;
        }
        if (pitch < -90.0f) {
            pitch = -90.0f;
        }
    }

    @Override
    public void keyboardInputCallback(int key, int action) {
        int myAction = KEY_UP;
        if (action != GLFW_RELEASE) {
            myAction = KEY_DOWN;
        }

        switch (key) {
            case GLFW_KEY_W:
                W_KEY_STATE = myAction;
                break;
            case GLFW_KEY_S:
                S_KEY_STATE = myAction;
                break;
            case GLFW_KEY_D:
                D_KEY_STATE = myAction;
                break;
            case GLFW_KEY_A:
                A_KEY_STATE = myAction;
                break;
            case GLFW_KEY_LEFT_SHIFT:
                LEFT_SHIT_STATE = myAction;
                break;
            case GLFW_KEY_SPACE:
                SPACE_STATE = myAction;
        }
    }
}
