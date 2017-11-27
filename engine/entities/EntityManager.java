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

    public int create() {
        return entityCount++;
    }
}
