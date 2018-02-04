package nl.sander758.gameclient.engine.loader.fileModel;

import nl.sander758.gameclient.engine.loader.ModelLoadingException;

import java.util.ArrayList;
import java.util.List;

public class Face {
    public List<Integer> vertexNormalCombinationIndices = new ArrayList<>();

    public void addVertex(int vertexNormalCombinationIndex) {
        vertexNormalCombinationIndices.add(vertexNormalCombinationIndex);
    }

    public List<Integer> toTriangles() throws ModelLoadingException {
        if (vertexNormalCombinationIndices.size() != 3) {
            throw new ModelLoadingException("Face found with more or less than 3 vertices");
        }

        return vertexNormalCombinationIndices;
    }
}
