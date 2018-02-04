package nl.sander758.gameclient.engine.waterSystem;

import nl.sander758.gameclient.engine.entitySystem.EntityNotFoundException;

import java.util.Collection;
import java.util.HashMap;

public class WaterEntityRegistry {

    private static HashMap<Integer, WaterEntity> entities = new HashMap<>();

    public static void addEntity(WaterEntity entity) {
        entities.put(entity.getId(), entity);
    }

    public static WaterEntity getEntity(int entityId) throws EntityNotFoundException {
        if (!entities.containsKey(entityId)) {
            throw new EntityNotFoundException();
        }
        return entities.get(entityId);
    }

    public static Collection<WaterEntity> getEntities() {
        return entities.values();
    }
}
