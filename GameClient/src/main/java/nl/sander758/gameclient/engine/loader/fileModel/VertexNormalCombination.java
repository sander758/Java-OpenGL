package nl.sander758.gameclient.engine.loader.fileModel;

public class VertexNormalCombination {

    public int id;

    public int vertexId;

    public int normalId;

    public VertexNormalCombination(int id, int vertexId, int normalId) {
        this.id = id;
        this.vertexId = vertexId;
        this.normalId = normalId;
    }

    public boolean equals(VertexNormalCombination combination) {
        return (combination.vertexId == vertexId && combination.normalId == normalId);
    }
}
