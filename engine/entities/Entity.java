package entities;

import models.RawModel;
import org.lwjgl.util.vector.Vector3f;

public class Entity {

    private final int id;

    private RawModel model;
    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    public Entity(int id, RawModel model, Vector3f position, Vector3f rotation, float scale) {
        this.id = id;
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public int getId() {
        return id;
    }

    public RawModel getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        rotation.x += dx;
        rotation.y += dy;
        rotation.z += dz;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
