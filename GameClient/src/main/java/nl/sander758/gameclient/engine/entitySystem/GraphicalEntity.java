package nl.sander758.gameclient.engine.entitySystem;

import nl.sander758.gameclient.engine.loader.Model;
import org.joml.Vector3f;

public class GraphicalEntity extends Entity {

    private Model model;

    private Vector3f rotation;

    private float scale;

    public GraphicalEntity(Model model, Vector3f location, Vector3f rotation, float scale) {
        super(location);
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void increaseRotation(float rx, float ry, float rz) {
        rotation.x += rx;
        rotation.y += ry;
        rotation.z += rz;
    }

    public float getScale() {
        return scale;
    }
}
