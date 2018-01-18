package nl.sander758.gameclient.engine.loader;

public class MeshLoader {

    public static Mesh load3DMesh(float[] vertices, float[] normals, float[] colors, int[] indices) {
        Mesh mesh = new Mesh();
        mesh.bindVAO();
        mesh.attachVBO(new VBO(0, 3, vertices));
        mesh.attachVBO(new VBO(1, 3, normals));
        mesh.attachVBO(new VBO(2, 3, colors));
        mesh.attachVBO(new VBO(indices));
        mesh.unbindVAO();
        mesh.setVertexCount(indices.length);
        return mesh;
    }

    public static Mesh loadWaterMesh(float[] positions, byte[] indicators) {
        Mesh mesh = new Mesh();
        mesh.bindVAO();
        mesh.attachVBO(new VBO(0, 2, positions));
        mesh.attachVBO(new VBO(1, 4, indicators));
        mesh.unbindVAO();
        mesh.setVertexCount(positions.length / 2);
        return mesh;
    }

    public static Mesh loadQuad() {
        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };

        Mesh mesh = new Mesh();
        mesh.bindVAO();
        mesh.attachVBO(new VBO(0, 2, positions));
        mesh.unbindVAO();
        mesh.setVertexCount(positions.length / 2);
        return mesh;
    }
}
