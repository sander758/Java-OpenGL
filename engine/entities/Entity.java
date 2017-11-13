package entities;

import models.RawModel;
import org.lwjgl.util.vector.Vector3f;

public class Entity {
    private final int id;
    private RawModel model;
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;

    public Entity(int id, RawModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.id = id;
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
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

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
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
        rotX += dx;
        rotY += dy;
        rotZ += dz;
    }

    public void setRotation(float dx, float dy, float dz) {
        rotX = dx;
        rotY = dy;
        rotZ = dz;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
