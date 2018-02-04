package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.Model;
import org.joml.Vector3f;

public abstract class ServerEntity extends StaticEntity {
    private final int clientId;

    public ServerEntity(Model model, int clientId) {
        super(model);
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setLocation(Vector3f location) {
        this.getLocation().x = location.x;
        this.getLocation().y = location.y;
        this.getLocation().z = location.z;
        updateTransformationMatrix();
    }
}
