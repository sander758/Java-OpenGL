package nl.sander758.gameclient.engine.waterSystem;

import nl.sander758.gameclient.engine.entitySystem.entities.EnvironmentalEntity;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import nl.sander758.gameclient.engine.loader.ModelRegistry;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class WaterEntity extends EnvironmentalEntity {

    public WaterEntity(Vector2f location, int radius) throws ModelNotFoundException {
        super(ModelRegistry.getModel("water_" + radius), new Vector3f(location.x, 0, location.y));
    }

    @Override
    public void updateTransformationMatrix() {
        Matrix4f matrix = getTransformationMatrix().identity();
        matrix.identity();
        matrix.translate(new Vector3f(getLocation().x, WaterRenderer.WATER_HEIGHT, getLocation().z));
    }
}
