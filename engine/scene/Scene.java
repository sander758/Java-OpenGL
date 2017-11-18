package scene;

import entities.Entity;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<Entity> entities = new ArrayList<>();

    private List<Terrain> terrains = new ArrayList<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }
}
