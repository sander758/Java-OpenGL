package renderer;

import fbos.Attachment;
import fbos.Fbo;
import fbos.RenderBufferAttachment;
import fbos.TextureAttachment;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Vector4f;
import scene.Camera;
import entities.Entity;
import scene.Light;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import scene.Scene;
import shadows.ShadowMapMasterRenderer;
import utils.DisplayManager;
import utils.Maths;
import utils.OpenGlUtils;
import water.WaterTile;

import java.util.List;


public class MasterRenderer {

    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private WaterRenderer waterRenderer;
    private ShadowMapMasterRenderer shadowMapMasterRenderer;
    private Matrix4f projectionMatrix;

    private final Fbo reflectionFbo;
    private final Fbo refractionFbo;

    public MasterRenderer(ShadowMapMasterRenderer shadowMapMasterRenderer) {
        projectionMatrix = Maths.createProjectionMatrix();

        entityRenderer = new EntityRenderer(projectionMatrix);
        terrainRenderer = new TerrainRenderer(projectionMatrix, shadowMapMasterRenderer.getShadowDistance(), shadowMapMasterRenderer.getShadowMapSize());
        waterRenderer = new WaterRenderer(projectionMatrix);

        this.shadowMapMasterRenderer = shadowMapMasterRenderer;


        int displayWidth = (int) DisplayManager.WIDTH;
        int displayHeight = (int) DisplayManager.HEIGHT;

        this.reflectionFbo = createWaterFbo(displayWidth, displayHeight, false);
        this.refractionFbo = createWaterFbo(displayWidth / 2, displayHeight / 2, true);
    }

    /**
     * Main render.
     *
     * @param scene
     * @param camera
     * @param light
     * @param clipPlane
     */
    public void render(Scene scene, Camera camera, Light light, Vector4f clipPlane) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapMasterRenderer.getShadowMap());


        entityRenderer.render(scene.getEntities(), camera, light, clipPlane);
        terrainRenderer.render(scene.getTerrains(), camera, light, clipPlane, shadowMapMasterRenderer.getToShadowMapSpaceMatrix(), true);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, reflectionFbo.getColourBuffer(0));
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, refractionFbo.getColourBuffer(0));
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, refractionFbo.getDepthBuffer());
        waterRenderer.render(scene.getWaterTiles(), camera, light);
    }

    public void renderShadowMap(List<Entity> entities, Vector3f lightDirection) {
        shadowMapMasterRenderer.render(entities, lightDirection);
    }

    public void doReflectionPass(Scene scene, Camera camera, Light light, Vector4f clipPlane) {
        reflectionFbo.bindForRender(0);
        float distance = 2 * (camera.getPosition().y - WaterTile.WATER_HEIGHT);
        camera.getPosition().y -= distance;
        camera.invertPitch();
        prepare();
        entityRenderer.render(scene.getEntities(), camera, light, clipPlane);
        terrainRenderer.render(scene.getTerrains(), camera, light, clipPlane, shadowMapMasterRenderer.getToShadowMapSpaceMatrix(), false);
        camera.invertPitch();
        camera.getPosition().y += distance;
        reflectionFbo.unbindAfterRender();
    }

    public void doRefractionPass(Scene scene, Camera camera, Light light, Vector4f clipPlane) {
        refractionFbo.bindForRender(0);
        prepare();
        entityRenderer.render(scene.getEntities(), camera, light, clipPlane);
        terrainRenderer.render(scene.getTerrains(), camera, light, clipPlane, shadowMapMasterRenderer.getToShadowMapSpaceMatrix(), false);
        refractionFbo.unbindAfterRender();
    }

    private void prepare() {
        GL11.glClearColor(Engine.skyColor.x, Engine.skyColor.y, Engine.skyColor.z, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
        OpenGlUtils.cullBackFaces(true);
        OpenGlUtils.enableDepthTesting(true);
        OpenGlUtils.antialias(true);
    }

    private static Fbo createWaterFbo(int width, int height, boolean useTextureForDepth) {
        Attachment colourAttach = new TextureAttachment(GL11.GL_RGBA8);
        Attachment depthAttach;
        if (useTextureForDepth) {
            depthAttach = new TextureAttachment(GL14.GL_DEPTH_COMPONENT24);
        } else {
            depthAttach = new RenderBufferAttachment(GL14.GL_DEPTH_COMPONENT24);
        }
        return Fbo.newFbo(width, height).addColourAttachment(0, colourAttach).addDepthAttachment(depthAttach).init();
    }

    public void cleanUp() {
        entityRenderer.cleanUp();
        terrainRenderer.cleanUp();
        waterRenderer.cleanUp();
        reflectionFbo.delete();
        refractionFbo.delete();
    }
}
