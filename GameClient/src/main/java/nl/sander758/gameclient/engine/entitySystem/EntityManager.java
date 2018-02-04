package nl.sander758.gameclient.engine.entitySystem;

public class EntityManager {

    private static int entityCount = 0;

    public static int getEntityId() {
        return entityCount++;
    }
}
