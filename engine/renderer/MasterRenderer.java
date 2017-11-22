package renderer;

import entities.Camera;
import entities.Entity;
import entities.Light;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import scene.Scene;
import shadows.ShadowMapMasterRenderer;

import java.util.List;


public class MasterRenderer {

    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private ShadowMapMasterRenderer shadowMapMasterRenderer;

    public MasterRenderer(ShadowMapMasterRenderer shadowMapMasterRenderer) {
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer(shadowMapMasterRenderer.getShadowDistance());

        this.shadowMapMasterRenderer = shadowMapMasterRenderer;
    }

    public void render(Scene scene, Camera camera, Light light, Matrix4f toShadowSpace) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.4667f, 0.901f, 1f, 1.0f);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapMasterRenderer.getShadowMap());


        entityRenderer.render(scene.getEntities(), camera, light);
        terrainRenderer.render(scene.getTerrains(), camera, light, toShadowSpace);
    }

    public void renderShadowMap(List<Entity> entities, Vector3f lightDirection) {
        shadowMapMasterRenderer.render(entities, lightDirection);
    }

    public void cleanUp() {
        entityRenderer.cleanUp();
    }
}
