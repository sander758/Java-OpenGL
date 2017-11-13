package scene;

import entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<Entity> entities = new ArrayList<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
