package nl.sander758.gameclient.engine.loader;

import java.util.ArrayList;
import java.util.List;

public abstract class Model {

    protected List<Mesh> meshes = new ArrayList<>();

    protected abstract void load() throws ModelLoadingException;

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public void cleanUp() {
        for (Mesh mesh : meshes) {
            mesh.cleanUp();
        }
    }
}
