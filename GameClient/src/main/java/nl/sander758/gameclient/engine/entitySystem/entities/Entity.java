package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.entitySystem.EntityManager;
import nl.sander758.gameclient.engine.loader.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Entity {
    private final int id;

    private final Model model;

    private Vector3f location = new Vector3f();

    private Matrix4f transformationMatrix = new Matrix4f();

    public abstract void updateTransformationMatrix();

    public Entity(Model model) {
        this.id = EntityManager.getEntityId();
        this.model = model;
    }

    public int getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Matrix4f getTransformationMatrix() {
        return transformationMatrix;
    }

    public void preRender() {
        //
    }

}
