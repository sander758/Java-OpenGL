package nl.sander758.gameclient.engine.entitySystem;

import nl.sander758.gameclient.engine.entitySystem.entities.StaticEntity;

import java.util.Collection;
import java.util.HashMap;

public class StaticEntityRegistry {

    private static HashMap<Integer, StaticEntity> entities = new HashMap<>();

    public static void addEntity(StaticEntity entity) {
        entities.put(entity.getId(), entity);
    }

    public static StaticEntity getEntity(int entityId) throws EntityNotFoundException {
        if (!entities.containsKey(entityId)) {
            throw new EntityNotFoundException();
        }
        return entities.get(entityId);
    }

    public static Collection<StaticEntity> getEntities() {
        return entities.values();
    }
}
