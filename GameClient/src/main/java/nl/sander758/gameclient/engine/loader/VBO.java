package nl.sander758.gameclient.engine.loader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class VBO {

    private int vboID;

    private VBOType vboType;

    private int attributeNumber;
    private int size;

    private float[] attributeData;

    private int[] indicesData;

    private byte[] byteData;

    /**
     * Attribute data
     */
    public VBO(int attributeNumber, int size, float[] data) {
        this.vboType = VBOType.DATA_FLOAT;
        this.attributeNumber = attributeNumber;
        this.size = size;
        this.attributeData = data;
    }

    public VBO(int attributeNumber, int size, byte[] byteData) {
        this.vboType = VBOType.DATA_BYTE;
        this.attributeNumber = attributeNumber;
        this.size = size;
        this.byteData = byteData;
    }

    /**
     * Indices data
     */
    public VBO(int[] indices) {
        this.vboType = VBOType.INDICES_DATA;
        this.indicesData = indices;
    }

    public void store() {
        vboID = GL15.glGenBuffers();
        switch (vboType) {
            case DATA_FLOAT:
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(attributeData), GL15.GL_STATIC_DRAW);
                GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                break;

            case DATA_BYTE:
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createByteBuffer(byteData), GL15.GL_STATIC_DRAW);
                GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_BYTE, false, 0, 0);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                break;

            case INDICES_DATA:
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
                GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(indicesData), GL15.GL_STATIC_DRAW);
                break;

        }
    }

    public void cleanUp() {
        GL15.glDeleteBuffers(vboID);
    }


    public VBOType getVboType() {
        return vboType;
    }

    /**
     * Creates a float buffer with all the data in the right order.
     *
     * @param vectors the float data.
     * @return The float buffer.
     */
    private FloatBuffer createFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Creates a int buffer with all ints in the right order.
     *
     * @param indices the indices or just a list of ints.
     * @return The int buffer.
     */
    private IntBuffer createIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private ByteBuffer createByteBuffer(byte[] bytes) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }
}
