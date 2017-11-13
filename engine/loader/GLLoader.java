package loader;

import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class GLLoader {

    private static GLLoader modelLoader = new GLLoader();

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();

    private GLLoader() {
        // private constructor
    }

    public static GLLoader getLoader() {
        return modelLoader;
    }

    public RawModel loadToVao(float[] positions, float[] colors, float[] normals, int[] indices, String name) {
        int vaoID = createVAO();
        this.bindIndicesBuffer(indices);
        this.storeDataInAttributeList(0, 3, positions);
        this.storeDataInAttributeList(1, 3, colors);
        this.storeDataInAttributeList(2, 3, normals);
        this.unbindVAO();
        return new RawModel(vaoID, indices.length, name);
    }

    public RawModel loadToVao(float[] positions, String name) {
        int vaoID = createVAO();
        this.storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / 2, name);
    }

    public void cleanUp() {
        for (int vao: this.vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo: this.vbos) {
            GL15.glDeleteBuffers(vbo);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        this.vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    // creates a vertex buffer object (vbo)
    private void storeDataInAttributeList(int attributeNumber, int size, float[] data) {
        int vboID = GL15.glGenBuffers();
        this.vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = this.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        this.vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
