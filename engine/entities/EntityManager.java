package entities;

import models.RawModel;
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;

public class EntityManager {
    private static EntityManager manager = new EntityManager();

    private HashMap<Integer, Entity> entities = new HashMap<>();
    private int entityCount = 0;

    private EntityManager(){}

    public static EntityManager getManager() {
        return manager;
    }

    public Entity create(RawModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {

        entityCount++;
        int entityId = entityCount;

//        entities.put(entityId, entity);

        return new Entity(entityId, model, position, rotX, rotY, rotZ, scale);
    }

    public Entity getEntity(int entityId) {
        return entities.get(entityId);
    }

    public HashMap<Integer, Entity> getEntities() {
        return entities;
    }

    public void removeEntity(int entityId) {
        entities.remove(entityId);
    }
}
