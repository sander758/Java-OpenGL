package nl.sander758.gameclient.engine.waterSystem;

import nl.sander758.gameclient.engine.loader.GeneratedModel;
import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.loader.VBO;
import org.joml.Vector2f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WaterModel extends GeneratedModel {

    private int radius;

    public WaterModel(int radius) {
        this.radius = radius;
    }

    @Override
    public void generate() {
        int size = radius * 2;

        int totalVertexCount = size * size * 6;

        float[] positions = new float[totalVertexCount * 2];
        ByteBuffer buffer = ByteBuffer.allocate(totalVertexCount * 4).order(ByteOrder.nativeOrder());

        int pointer = 0;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                Vector2f bottomLeft = new Vector2f(x, z);
                Vector2f bottomRight = new Vector2f(x + 1, z);
                Vector2f topLeft = new Vector2f(x, z + 1);
                Vector2f topRight = new Vector2f(x + 1, z + 1);

                positions = addTriangle(positions, pointer, bottomLeft, topLeft, topRight, buffer);
                pointer++;
                positions = addTriangle(positions, pointer, bottomLeft, topRight, bottomRight, buffer);
                pointer++;
            }
        }

        Mesh mesh = new Mesh();
        mesh.bindVAO();
        mesh.attachVBO(new VBO(0, 2, positions));
        mesh.attachVBO(new VBO(1, 4, buffer.array()));
        mesh.unbindVAO();
        mesh.setVertexCount(positions.length / 2);

        meshes.add(mesh);
    }

    private float[] addTriangle(float[] positions, int pointer, Vector2f vertexA, Vector2f vertexB, Vector2f vertexC, ByteBuffer buffer) {
        positions[pointer * 6] = vertexA.x;
        positions[pointer * 6 + 1] = vertexA.y;
        addIndicators(vertexA, vertexB, vertexC, buffer);

        positions[pointer * 6 + 2] = vertexB.x;
        positions[pointer * 6 + 3] = vertexB.y;
        addIndicators(vertexB, vertexC, vertexA, buffer);

        positions[pointer * 6 + 4] = vertexC.x;
        positions[pointer * 6 + 5] = vertexC.y;
        addIndicators(vertexC, vertexA, vertexB, buffer);

        return positions;
    }

    private void addIndicators(Vector2f vertex, Vector2f pointA, Vector2f pointB, ByteBuffer buffer) {
        Vector2f offset1 = pointA.sub(vertex, new Vector2f());
        Vector2f offset2 = pointB.sub(vertex, new Vector2f());

        buffer.put((byte) offset1.x);
        buffer.put((byte) offset1.y);
        buffer.put((byte) offset2.x);
        buffer.put((byte) offset2.y);
    }
}
