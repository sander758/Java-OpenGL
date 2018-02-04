package nl.sander758.gameclient.engine.loader.fileModel;

import org.joml.Vector3f;

public class Material {
    public String name;
    public Vector3f diffuseColor;

    public Material(String name, Vector3f diffuseColor) {
        this.name = name;
        this.diffuseColor = diffuseColor;
    }
}
