package nl.sander758.gameclient.engine.entitySystem;

import nl.sander758.gameclient.engine.loader.Mesh;
import org.joml.Vector3f;

public interface Renderable {
    // TODO maybe update to multiple meshes?
    Mesh getMesh();

    Vector3f getLocation();
}
