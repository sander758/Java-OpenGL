package nl.sander758.gameclient.engine.entitySystem.entities;

import org.joml.Vector3f;

public interface Rotatable {
    Vector3f getRotation();

    void setRotation(float x, float y, float z);

    void increaseRotation(float x, float y, float z);

    void setRotationX(float x);

    void setRotationY(float y);

    void setRotationZ(float z);
}
