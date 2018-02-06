package nl.sander758.gameclient.engine.player;

import nl.sander758.gameclient.engine.entitySystem.entities.Player;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import org.joml.Vector3f;

public class ServerPlayerEntity extends Player {

    private Vector3f previousLocation = new Vector3f();
    private long previousUpdateTime = 0;

    private Vector3f currentLocation = new Vector3f();
    private long currentUpdateTime = 0;

    public ServerPlayerEntity(int clientId) throws ModelNotFoundException {
        super(clientId);
    }

    public void updateState(long timestamp, Vector3f location) {
        previousLocation = currentLocation;
        previousUpdateTime = currentUpdateTime;

        currentLocation = location;
        currentUpdateTime = timestamp;
    }

    @Override
    public void preRender() {
        super.preRender();

        long clientTime = System.currentTimeMillis();

        int updateTime = (int) (currentUpdateTime - previousUpdateTime);
        long renderTime = clientTime - updateTime;
        if (renderTime < previousUpdateTime) {
            renderTime = previousUpdateTime;
        } else if (renderTime > currentUpdateTime) {
            renderTime = currentUpdateTime;
        }

        int partial = (int) (renderTime - previousUpdateTime);
        float percentage = (float) partial / (float) updateTime;

        Vector3f differenceLocations = currentLocation.sub(previousLocation, new Vector3f());

        getLocation().x = previousLocation.x + (percentage * differenceLocations.x);
        getLocation().y = previousLocation.y + (percentage * differenceLocations.y);
        getLocation().z = previousLocation.z + (percentage * differenceLocations.z);

        updateTransformationMatrix();
    }
}
