package nl.sander758.gameclient.client.entities.staticEntities;

import nl.sander758.gameclient.engine.entitySystem.entities.StaticEntity;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import nl.sander758.gameclient.engine.loader.ModelRegistry;
import org.joml.Vector3f;

public class Crossbow extends StaticEntity {

    public Crossbow(Vector3f location) throws ModelNotFoundException {
        super(ModelRegistry.getModel("crossbow"), location);
    }
}
