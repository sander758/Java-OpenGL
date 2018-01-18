package nl.sander758.gameclient.engine.scene;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Light {
    private Vector3f lightDirection;
    private Vector3f lightColor;
    private Vector2f bias;

    public Light(Vector3f lightDirection, Vector3f lightColor, Vector2f bias) {
        this.lightDirection = lightDirection;
        this.lightColor = lightColor;
        this.bias = bias;
    }

    public Vector3f getLightDirection() {
        return lightDirection;
    }

    public Vector3f getLightColor() {
        return lightColor;
    }

    public Vector2f getBias() {
        return bias;
    }
}
