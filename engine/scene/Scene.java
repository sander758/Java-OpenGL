package scene;

import entities.Entity;
import entities.Terrain;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<Entity> entities = new ArrayList<>();

    private List<Terrain> terrains = new ArrayList<>();

    private List<WaterTile> waterTiles = new ArrayList<>();

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

    public void addWaterTile(WaterTile waterTile) {
        waterTiles.add(waterTile);
    }

    public List<WaterTile> getWaterTiles() {
        return waterTiles;
    }
}
