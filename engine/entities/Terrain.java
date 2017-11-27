package entities;

import loader.GLLoader;
import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import utils.Maths;

public class Terrain extends Entity {
    private float[][] heights;

    private Vector2f size;
    private Vector2f sizeOffset;
    private RawModel heightModel;

    public Terrain(int id, RawModel model, Vector3f position, Vector3f rotation, float scale, float[][] heights, Vector2f size, Vector2f sizeOffset) {
        super(id, model, position, rotation, scale);
        this.heights = heights;
        this.size = size;
        this.sizeOffset = sizeOffset;

        this.heightModel = generateTerrain();
    }

    public Vector2f getSize() {
        return size;
    }

    public Vector2f getSizeOffset() {
        return sizeOffset;
    }

    public float[][] getHeights() {
        return heights;
    }

    public float getHeight(float x, float z) {
        int baseX = (int) (Math.floor(x) + sizeOffset.x);
        int baseZ = (int) (Math.floor(z) + sizeOffset.y);


        try {
            Vector3f pointA = new Vector3f(baseX, heights[baseX][baseZ], baseZ);
            Vector3f pointB = new Vector3f(baseX, heights[baseX][baseZ + 1], baseZ + 1);
            Vector3f pointC = new Vector3f(baseX + 1, heights[baseX + 1][baseZ], baseZ);
            Vector3f pointD = new Vector3f(baseX + 1, heights[baseX + 1][baseZ + 1], baseZ + 1);

            float height;

            if (1 - (x - Math.floor(x)) < (z - Math.floor(z))) {
                // 1
                height = Maths.baryCentric(pointA, pointB, pointC, new Vector2f(x + sizeOffset.x, z + sizeOffset.y));
            } else {
                // 2
                height = Maths.baryCentric(pointB, pointD, pointC, new Vector2f(x + sizeOffset.x, z + sizeOffset.y));
            }
            return height + 0.2f;
        } catch (IndexOutOfBoundsException e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
        }
        return 0;
    }

    private RawModel generateTerrain() {
        int size = (int) ((this.size.x + 1) * (this.size.y + 1));

        float[] positions = new float[size * 3];
        float[] colors = new float[size * 3];
        float[] normals = new float[size * 3];
        int[] indices = new int[6 * (int) this.size.x * (int) this.size.y];

        int vertexPointer = 0;
        for (int x = 0; x <= this.size.x; x++) {
            for (int z = 0; z <= this.size.y; z++) {
                positions[vertexPointer * 3] = x - this.sizeOffset.x;
                positions[vertexPointer * 3 + 1] = heights[x][z];
                positions[vertexPointer * 3 + 2] = z - this.sizeOffset.y;

                colors[vertexPointer * 3] = 1;
                colors[vertexPointer * 3 + 1] = 1;
                colors[vertexPointer * 3 + 2] = 1;

                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;

                vertexPointer++;
            }
        }

        int pointer = 0;
        for(int x = 0; x < this.size.x; x++) {
            for(int z = 0; z < this.size.y; z++) {
                int leftBottom = (int) (this.size.y + 1) * x + z;
                int leftTop = leftBottom + 1;
                int rightBottom = (int) (this.size.y + 1) * (x + 1) + z;
                int rightTop = rightBottom + 1;

                indices[pointer++] = leftBottom;
                indices[pointer++] = leftTop;
                indices[pointer++] = rightTop;

                indices[pointer++] = rightTop;
                indices[pointer++] = leftBottom;
                indices[pointer++] = rightBottom;
            }
        }
        return GLLoader.getLoader().loadToVao(positions, colors, normals, indices, "Terrain");
    }

    public RawModel getHeightModel() {
        return heightModel;
    }
}
