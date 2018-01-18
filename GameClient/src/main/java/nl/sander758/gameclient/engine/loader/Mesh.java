package nl.sander758.gameclient.engine.loader;

import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private final int vaoID;

    private List<VBO> vbos = new ArrayList<>();

    private int attributeCount = 0;

    private int vertexCount;

    public Mesh() {
        vaoID = GL30.glGenVertexArrays();
    }

    public void bindVAO() {
        GL30.glBindVertexArray(vaoID);
    }

    public void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    public List<VBO> getVbos() {
        return vbos;
    }

    public void cleanUp() {
        GL30.glDeleteVertexArrays(vaoID);
        for (VBO vbo : vbos) {
            vbo.cleanUp();
        }
    }

    public void attachVBO(VBO vbo) {
        vbo.store();
        vbos.add(vbo);
        if (vbo.getVboType() == VBOType.DATA_FLOAT) {
            attributeCount++;
        }
    }

    public int getAttributeCount() {
        return attributeCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
