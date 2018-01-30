package nl.sander758.gameclient.engine.player;

import nl.sander758.common.network.PacketIn;
import nl.sander758.common.network.PacketListener;
import nl.sander758.gameclient.network.packets.PlayerMovePacketOut;
import nl.sander758.gameclient.engine.input.InputManager;
import nl.sander758.gameclient.engine.input.KeyboardInputListener;
import nl.sander758.gameclient.engine.input.MouseInputListener;
import nl.sander758.gameclient.engine.utils.Timer;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class FirstPersonCamera implements Camera, MouseInputListener, KeyboardInputListener {

    private Vector3f position;
    private Vector3f newPosition = new Vector3f(0, 0, 0);
    private float pitch = 0;
    private float yaw = 0;

    private float speed = 10f;
    private float mouseSensitivity = 0.2f;

    private int KEY_UP = 0;
    private int KEY_DOWN = 1;

    private int W_KEY_STATE = 0;
    private int S_KEY_STATE = 0;
    private int D_KEY_STATE = 0;
    private int A_KEY_STATE = 0;
    private int LEFT_SHIT_STATE = 0;
    private int SPACE_STATE = 0;

    public FirstPersonCamera(Vector3f position) {
        this.position = position;
    }

    @Override
    public Vector3f getLocation() {
        return position;
    }

    @Override
    public Vector3f getNewLocation() {
        return newPosition;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public void invertPitch() {
        this.pitch = -pitch;
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    @Override
    public void update() {
        float newX = (float) (Math.sin(Math.toRadians(yaw)));
        float newZ = (float) (Math.cos(Math.toRadians(yaw)));

        float currentSpeed = Timer.getDeltaTime() * speed;

//        newPosition.x = 0;
//        newPosition.y = 0;
//        newPosition.z = 0;

        if (W_KEY_STATE == KEY_DOWN) {
            position.x += newX * currentSpeed;
            position.z -= newZ * currentSpeed;
        }
        if (S_KEY_STATE == KEY_DOWN) {
            position.x -= newX * currentSpeed;
            position.z += newZ * currentSpeed;
        }
        if (D_KEY_STATE == KEY_DOWN) {
            position.x += newZ * currentSpeed;
            position.z += newX * currentSpeed;
        }
        if (A_KEY_STATE == KEY_DOWN) {
            position.x -= newZ * currentSpeed;
            position.z -= newX * currentSpeed;
        }

        if (LEFT_SHIT_STATE == KEY_DOWN) {
            position.y -= currentSpeed;
        }
        if (SPACE_STATE == KEY_DOWN) {
            position.y += currentSpeed;
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

    @Override
    public void mouseInputCallback(double offsetX, double offsetY) {
        int state = InputManager.getMouseButtonState(GLFW_MOUSE_BUTTON_LEFT);
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

//    @Override
//    public void handle(PacketIn packet) {
//        if (packet.getId() != PacketIn.PacketType.ENTITY_MOVE_PACKET) {
//            return;
//        }
//        PlayerMovePacketOut entityMovePacket = (PlayerMovePacketOut) packet;
//        Vector3f newLocation = entityMovePacket.getLocation();
//        position.x += newLocation.x;
//        position.y += newLocation.y;
//        position.z += newLocation.z;
//    }
}
