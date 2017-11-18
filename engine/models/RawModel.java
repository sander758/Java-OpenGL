package models;

public class RawModel {
    private int vaoID;
    private int vertexCount;
    private String name;
    private ModelType type;

    public RawModel(int vaoID, int vertexCount, String name) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.name = name;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public String getName() {
        return name;
    }

    public void setType(ModelType type) {
        this.type = type;
    }
}
