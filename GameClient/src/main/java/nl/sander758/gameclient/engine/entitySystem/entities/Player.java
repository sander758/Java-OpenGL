package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Player extends ServerEntity implements Nameable, Rotatable {

    private String name = "";
    private Vector3f rotation = new Vector3f();

    public Player(Model model) {
        super(model);
    }

    @Override
    public void updateTransformationMatrix() {
        Matrix4f matrix = getTransformationMatrix().identity();
        matrix.translate(getLocation());
        matrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        matrix.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
        updateTransformationMatrix();
    }

    @Override
    public void increaseRotation(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
        updateTransformationMatrix();
    }

    @Override
    public void setRotationX(float x) {
        rotation.x = x;
    }

    @Override
    public void setRotationY(float y) {
        rotation.y = y;
    }

    @Override
    public void setRotationZ(float z) {
        rotation.z = z;
    }
}
