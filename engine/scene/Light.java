package scene;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Light {
    private Vector3f direction;
    private Vector3f color;
    private Vector2f bias;

    public Light(Vector3f direction, Vector3f color, Vector2f bias) {
        this.direction = direction;
        this.color = color;
        this.bias = bias;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector2f getBias() {
        return bias;
    }
}
