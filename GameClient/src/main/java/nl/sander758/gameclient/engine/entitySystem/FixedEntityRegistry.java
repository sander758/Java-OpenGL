package nl.sander758.gameclient.engine.entitySystem;

import nl.sander758.gameclient.engine.entitySystem.entities.FixedEntity;

import java.util.Collection;
import java.util.HashMap;

public class FixedEntityRegistry {

    private static HashMap<Integer, FixedEntity> entities = new HashMap<>();

    public static void addEntity(FixedEntity entity) {
        entities.put(entity.getId(), entity);
    }

    public static FixedEntity getEntity(int entityId) throws EntityNotFoundException {
        if (!entities.containsKey(entityId)) {
            throw new EntityNotFoundException();
        }
        return entities.get(entityId);
    }

    public static Collection<FixedEntity> getEntities() {
        return entities.values();
    }
}
