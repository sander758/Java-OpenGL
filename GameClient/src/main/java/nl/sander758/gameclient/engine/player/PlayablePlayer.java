package nl.sander758.gameclient.engine.player;

import nl.sander758.gameclient.engine.entitySystem.entities.Collidable;
import nl.sander758.gameclient.engine.entitySystem.entities.Controllable;
import nl.sander758.gameclient.engine.entitySystem.entities.Player;
import nl.sander758.gameclient.engine.guiSystem.chat.ChatManager;
import nl.sander758.gameclient.engine.input.InputManager;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import nl.sander758.gameclient.engine.utils.Timer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class PlayablePlayer extends Player implements Collidable, Controllable {

    private int KEY_UP = 0;
    private int KEY_DOWN = 1;

    private float speed = 10f;
    private float mouseSensitivity = 0.2f;

    private int wKeyState = 0;
    private int sKeyState = 0;
    private int dKeyState = 0;
    private int aKeyState = 0;
    private int leftShiftState = 0;
    private int spaceState = 0;

    private float yaw = 0;
    private float pitch = 0;

    private Camera activeCamera;

    public PlayablePlayer(int clientId) throws ModelNotFoundException {
        super(clientId);

        activeCamera = new FirstPersonCamera();
    }

    public void update() {
        float newX = (float) (Math.sin(Math.toRadians(yaw)));
        float newZ = (float) (Math.cos(Math.toRadians(yaw)));

        float currentSpeed = Timer.getDeltaTime() * speed;

        Vector3f location = getLocation();

        if (wKeyState == KEY_DOWN) {
            location.x += newX * currentSpeed;
            location.z -= newZ * currentSpeed;
        }
        if (sKeyState == KEY_DOWN) {
            location.x -= newX * currentSpeed;
            location.z += newZ * currentSpeed;
        }
        if (dKeyState == KEY_DOWN) {
            location.x += newZ * currentSpeed;
            location.z += newX * currentSpeed;
        }
        if (aKeyState == KEY_DOWN) {
            location.x -= newZ * currentSpeed;
            location.z -= newX * currentSpeed;
        }

        if (leftShiftState == KEY_DOWN) {
            location.y -= currentSpeed;
        }
        if (spaceState == KEY_DOWN) {
            location.y += currentSpeed;
        }

        updateTransformationMatrix();
        activeCamera.updateViewMatrix(getLocation(), yaw, pitch);
    }

    public void invertPitch() {
        this.pitch = -pitch;
        activeCamera.updateViewMatrix(getLocation(), yaw, pitch);
    }

    @Override
    public void keyboardInputCallback(int key, int keyAction) {
        int action = KEY_UP;
        if (keyAction != GLFW_RELEASE && !ChatManager.getManager().hasChatOpen()) {
            action = KEY_DOWN;
        }

        switch (key) {
            case GLFW_KEY_W:
                wKeyState = action;
                break;
            case GLFW_KEY_S:
                sKeyState = action;
                break;
            case GLFW_KEY_D:
                dKeyState = action;
                break;
            case GLFW_KEY_A:
                aKeyState = action;
                break;
            case GLFW_KEY_LEFT_SHIFT:
                leftShiftState = action;
                break;
            case GLFW_KEY_SPACE:
                spaceState = action;
        }
    }

    @Override
    public void mouseInputCallback(double offsetX, double offsetY) {
        int state = InputManager.getMouseButtonState(GLFW_MOUSE_BUTTON_LEFT);
        if (state != GLFW_PRESS) {
            return;
        }

        pitch += -offsetY * mouseSensitivity;
        yaw = (yaw + (float) offsetX * mouseSensitivity) % 360;


        if (pitch > 90.0f) {
            pitch = 90.0f;
        }
        if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        activeCamera.updateViewMatrix(getLocation(), yaw, pitch);
    }

    @Override
    public Matrix4f getViewMatrix() {
        return activeCamera.getViewMatrix();
    }
}
