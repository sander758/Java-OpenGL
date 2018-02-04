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
        int size = vertexNormalCombinationIndices.size();
        if (size < 3) {
            throw new ModelLoadingException("Face found with less then 3 vertices");
        }
        List<Integer> indices = new ArrayList<>();

        //todo make better algorithm
        switch (size) {
            case 3:
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(1));
                indices.add(vertexNormalCombinationIndices.get(2));
                break;
            case 4:
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(1));
                indices.add(vertexNormalCombinationIndices.get(2));

                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(3));
                indices.add(vertexNormalCombinationIndices.get(1));
                break;
            case 5:
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(1));
                indices.add(vertexNormalCombinationIndices.get(2));

                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(3));
                indices.add(vertexNormalCombinationIndices.get(4));

                indices.add(vertexNormalCombinationIndices.get(4));
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(2));
                break;
            case 6:
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(1));
                indices.add(vertexNormalCombinationIndices.get(2));

                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(3));
                indices.add(vertexNormalCombinationIndices.get(4));

                indices.add(vertexNormalCombinationIndices.get(4));
                indices.add(vertexNormalCombinationIndices.get(5));
                indices.add(vertexNormalCombinationIndices.get(0));

                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(4));
                break;
            case 7:
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(1));
                indices.add(vertexNormalCombinationIndices.get(2));

                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(3));
                indices.add(vertexNormalCombinationIndices.get(4));

                indices.add(vertexNormalCombinationIndices.get(4));
                indices.add(vertexNormalCombinationIndices.get(5));
                indices.add(vertexNormalCombinationIndices.get(6));

                indices.add(vertexNormalCombinationIndices.get(6));
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(2));

                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(4));
                indices.add(vertexNormalCombinationIndices.get(6));
                break;
            case 8:
                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(1));
                indices.add(vertexNormalCombinationIndices.get(2));

                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(3));
                indices.add(vertexNormalCombinationIndices.get(4));

                indices.add(vertexNormalCombinationIndices.get(4));
                indices.add(vertexNormalCombinationIndices.get(5));
                indices.add(vertexNormalCombinationIndices.get(6));

                indices.add(vertexNormalCombinationIndices.get(6));
                indices.add(vertexNormalCombinationIndices.get(7));
                indices.add(vertexNormalCombinationIndices.get(0));

                indices.add(vertexNormalCombinationIndices.get(0));
                indices.add(vertexNormalCombinationIndices.get(2));
                indices.add(vertexNormalCombinationIndices.get(4));

                indices.add(vertexNormalCombinationIndices.get(4));
                indices.add(vertexNormalCombinationIndices.get(6));
                indices.add(vertexNormalCombinationIndices.get(0));
                break;
            default:
                throw new ModelLoadingException("Unsupported amount of vertices in face. Size of face: " + size);
        }

        return indices;
    }
}
