package nl.sander758.gameclient.engine.terrainSystem;

import nl.sander758.gameclient.engine.entitySystem.GraphicalEntity;
import nl.sander758.gameclient.engine.loader.Model;
import nl.sander758.gameclient.engine.utils.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Terrain extends GraphicalEntity {

    private float[][] heights;

    private Vector2f size;
    private Vector2f sizeOffset;


    public Terrain(Model model, Vector3f location, Vector3f rotation, float scale) {
        super(model, location, rotation, scale);
        heights = model.getHeights();
        sizeOffset = model.getSizeOffset();
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

    public Vector2f getSizeOffset() {
        return sizeOffset;
    }
}
