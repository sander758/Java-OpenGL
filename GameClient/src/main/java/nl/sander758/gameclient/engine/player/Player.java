package nl.sander758.gameclient.engine.player;

import nl.sander758.gameclient.engine.input.InputManager;
import org.joml.Vector3f;

import java.util.HashMap;

public class Player {

    private CameraMode mode;

    private HashMap<CameraMode, Camera> cameras = new HashMap<>();

    Player() {
        this.mode = CameraMode.FIRST_PERSON;

        FirstPersonCamera firstPersonCamera = new FirstPersonCamera(new Vector3f(0, 0, 0));
        cameras.put(CameraMode.FIRST_PERSON, firstPersonCamera);
        InputManager.registerMouseInputListener(firstPersonCamera);
        InputManager.registerKeyboardInputListener(firstPersonCamera);
//        PacketListenerRegistry.register(PacketType.ENTITY_MOVE_PACKET, firstPersonCamera);

//        cameras.put(CameraMode.THIRD_PERSON, new ThirdPersonCamera());
    }

    public void changeCameraMode(CameraMode mode) {
        if (!cameras.containsKey(mode)) {
            throw new UnsupportedOperationException("Unsupported camera mode");
        }
        this.mode = mode;
    }

    public Camera getCamera() {
        return cameras.get(mode);
    }

    public Vector3f getPosition() {
        return cameras.get(mode).getLocation();
    }

}
