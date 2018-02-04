package nl.sander758.gameclient.engine.loader;

import org.lwjgl.opengl.GL20;
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

    public void attachVBO(VBO vbo) {
        vbo.store();
        vbos.add(vbo);
        switch (vbo.getVboType()) {
            case FLOAT_DATA:
            case BYTE_DATA:
                attributeCount++;
                break;
        }
    }

    public void bindVAO() {
        GL30.glBindVertexArray(vaoID);
    }

    public void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void prepareRender() {
        bindVAO();
        bindAttributes();
    }

    public void endRender() {
        unbindAttributes();
        unbindVAO();
    }

    public void cleanUp() {
        GL30.glDeleteVertexArrays(vaoID);
        for (VBO vbo : vbos) {
            vbo.cleanUp();
        }
    }

    private void bindAttributes() {
        for (int i = 0; i < attributeCount; i++) {
            GL20.glEnableVertexAttribArray(i);
        }
    }

    private void unbindAttributes() {
        for (int i = attributeCount; i > 0; i--) {
            GL20.glDisableVertexAttribArray(i);
        }
    }
}
