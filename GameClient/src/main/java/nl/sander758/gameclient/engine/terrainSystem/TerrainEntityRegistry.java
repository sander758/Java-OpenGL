package nl.sander758.gameclient.engine.terrainSystem;

import nl.sander758.gameclient.engine.entitySystem.EntityNotFoundException;
import nl.sander758.gameclient.engine.entitySystem.entities.TerrainEntity;

import java.util.Collection;
import java.util.HashMap;

public class TerrainEntityRegistry {

    private static HashMap<Integer, TerrainEntity> entities = new HashMap<>();

    public static void addEntity(TerrainEntity entity) {
        entities.put(entity.getId(), entity);
    }

    public static TerrainEntity getEntity(int entityId) throws EntityNotFoundException {
        if (!entities.containsKey(entityId)) {
            throw new EntityNotFoundException();
        }
        return entities.get(entityId);
    }

    public static Collection<TerrainEntity> getEntities() {
        return entities.values();
    }
}
