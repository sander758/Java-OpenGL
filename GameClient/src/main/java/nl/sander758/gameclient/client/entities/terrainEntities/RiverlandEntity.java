package nl.sander758.gameclient.client.entities.terrainEntities;

import nl.sander758.gameclient.engine.entitySystem.entities.TerrainEntity;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import nl.sander758.gameclient.engine.loader.ModelRegistry;
import org.joml.Vector3f;

public class RiverlandEntity extends TerrainEntity {

    public RiverlandEntity(Vector3f location) throws ModelNotFoundException {
        super(ModelRegistry.getModel("riverland"), location);
    }
}
