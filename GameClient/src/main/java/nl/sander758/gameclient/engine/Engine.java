package nl.sander758.gameclient.engine;

import nl.sander758.gameclient.engine.display.Camera;
import nl.sander758.gameclient.engine.display.WindowManager;
import nl.sander758.gameclient.engine.entitySystem.EntityRenderer;
import nl.sander758.gameclient.engine.entitySystem.GraphicalEntity;
import nl.sander758.gameclient.engine.fbos.Attachment;
import nl.sander758.gameclient.engine.fbos.Fbo;
import nl.sander758.gameclient.engine.fbos.RenderBufferAttachment;
import nl.sander758.gameclient.engine.fbos.TextureAttachment;
import nl.sander758.gameclient.engine.guiSystem.GuiRenderer;
import nl.sander758.gameclient.engine.guiSystem.GuiTexture;
import nl.sander758.gameclient.engine.input.InputManager;
import nl.sander758.gameclient.engine.loader.Model;
import nl.sander758.gameclient.engine.loader.ModelRegistry;
import nl.sander758.gameclient.engine.scene.Light;
import nl.sander758.gameclient.engine.shadowSystem.ShadowMapMasterRenderer;
import nl.sander758.gameclient.engine.terrainSystem.Terrain;
import nl.sander758.gameclient.engine.terrainSystem.TerrainRenderer;
import nl.sander758.gameclient.engine.utils.Maths;
import nl.sander758.gameclient.engine.utils.OpenGlUtils;
import nl.sander758.gameclient.engine.waterSystem.WaterRenderer;
import nl.sander758.gameclient.engine.waterSystem.WaterTile;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Engine package responsibility is for all the rendering, loading data to openGL, shaders, fbo
 */
public class Engine {

    private Fbo reflectionFbo;
    private Fbo refractionFbo;

    private Camera camera;
    private Light light;

    private ShadowMapMasterRenderer shadowMapMasterRenderer;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private WaterRenderer waterRenderer;
    private GuiRenderer guiRenderer;

    private List<GraphicalEntity> entities = new ArrayList<>();
    private List<Terrain> terrains = new ArrayList<>();
    private List<WaterTile> waterTiles = new ArrayList<>();
    private List<GuiTexture> guiTextures = new ArrayList<>();

    public void init() {
        WindowManager.init();
        InputManager.register();
    }

    public void start() {
        int displayWidth = WindowManager.getWidth();
        int displayHeight = WindowManager.getHeight();

        reflectionFbo = createWaterFbo(displayWidth, displayHeight, false);
        refractionFbo = createWaterFbo(displayWidth / 2, displayHeight / 2, true);

        camera = new Camera(new Vector3f(0, 2, 0));
        InputManager.registerMouseInputListener(camera);
        InputManager.registerKeyboardInputListener(camera);

        light = new Light(new Vector3f(0.8f, -0.8f, 0.2f), new Vector3f(1f, 1f, 1f), new Vector2f(0.3f, 0.8f));

        Matrix4f projectionMatrix = Maths.createProjectionMatrix();
        shadowMapMasterRenderer = new ShadowMapMasterRenderer(camera);
        entityRenderer = new EntityRenderer(projectionMatrix);
        terrainRenderer = new TerrainRenderer(projectionMatrix, shadowMapMasterRenderer.getShadowDistance(), shadowMapMasterRenderer.getShadowMapSize());
        waterRenderer = new WaterRenderer(projectionMatrix);
        guiRenderer = new GuiRenderer();

        loop();

        cleanUp();

        WindowManager.destroy();
    }

    private void loop() {
        Model tree = ModelRegistry.getModel("simple_tree");
        Model velociraptor = ModelRegistry.getModel("velociraptor");
        Model riverland = ModelRegistry.getModel("riverland");

        GraphicalEntity treeEntity = new GraphicalEntity(tree, new Vector3f(0, -1, -6), new Vector3f(0, 0, 0), 1);
        GraphicalEntity velociraptorEntity = new GraphicalEntity(velociraptor, new Vector3f(0, 0, 0), new Vector3f(0, 60, 0), 1);
        entities.add(treeEntity);
        entities.add(velociraptorEntity);

        Terrain terrain1 = new Terrain(riverland, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1);
        terrains.add(terrain1);

        WaterTile tile = new WaterTile(new Vector2f(0, 0), 16, 1);
        waterTiles.add(tile);

//        guiTextures.add(new GuiTexture(shadowMapMasterRenderer.getShadowMap(), new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));
//        guiTextures.add(new GuiTexture(reflectionFbo.getColourBuffer(0), new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));
//        guiTextures.add(new GuiTexture(refractionFbo.getColourBuffer(0), new Vector2f(0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));

        OpenGlUtils.enableDepthTesting(true);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(WindowManager.getWindow()) ) {
            // Poll for window events. The key keyboardInputCallback above will only be
            // invoked during this call.
            glfwPollEvents();
            camera.move(terrains);

//            treeEntity.increaseRotation(0, 1, 0);

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            doReflectionRender(new Vector4f(0, 1, 0, -WaterTile.WATER_HEIGHT + WaterTile.REFLECT_OFFSET));
            doRefractionRender(new Vector4f(0, -1, 0, WaterTile.WATER_HEIGHT + WaterTile.REFRACT_OFFSET));
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

            doMainRender(new Vector4f(0, 0, 0, 0));

            glfwSwapBuffers(WindowManager.getWindow()); // swap the color buffers
        }
    }

    private void prepareRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.4667f, 0.901f, 1f, 1.0f);
        GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
        OpenGlUtils.cullBackFaces(true);
        OpenGlUtils.antialias(true);
    }

    private void doReflectionRender(Vector4f clipPlane) {
        reflectionFbo.bindForRender(0);
        float distance = 2 * (camera.getPosition().y - WaterTile.WATER_HEIGHT);
        camera.getPosition().y -= distance;
        camera.invertPitch();
        prepareRender();
        entityRenderer.render(entities, camera, light, clipPlane);
        terrainRenderer.render(terrains, camera, light, clipPlane, new Matrix4f(), false);
        camera.invertPitch();
        camera.getPosition().y += distance;
        reflectionFbo.unbindAfterRender();
    }

    private void doRefractionRender(Vector4f clipPlane) {
        refractionFbo.bindForRender(0);
        prepareRender();
        entityRenderer.render(entities, camera, light, clipPlane);
        terrainRenderer.render(terrains, camera, light, clipPlane, new Matrix4f(), false);
        refractionFbo.unbindAfterRender();
    }

    private void doMainRender(Vector4f clipPlane) {
        prepareRender();

//        shadowMapMasterRenderer.render(entities, light.getLightDirection());

        entityRenderer.render(entities, camera, light, clipPlane);
        terrainRenderer.bindTextures(shadowMapMasterRenderer.getShadowMap());
        terrainRenderer.render(terrains, camera, light, clipPlane, new Matrix4f(), false);

        waterRenderer.bindTextures(reflectionFbo.getColourBuffer(0), refractionFbo.getColourBuffer(0), refractionFbo.getDepthBuffer());
        waterRenderer.render(waterTiles, camera, light);
        guiRenderer.render(guiTextures);
    }

    private void cleanUp() {
        entityRenderer.cleanUp();
        terrainRenderer.cleanUp();
        waterRenderer.cleanUp();
        guiRenderer.cleanUp();
        shadowMapMasterRenderer.cleanUp();
        reflectionFbo.delete();
        refractionFbo.delete();
        for (GraphicalEntity graphicalEntity : entities) {
            graphicalEntity.getModel().getMesh().cleanUp();
        }
    }

    private Fbo createWaterFbo(int width, int height, boolean useTextureForDepth) {
        Attachment colourAttach = new TextureAttachment(GL11.GL_RGBA8);
        Attachment depthAttach;
        if (useTextureForDepth) {
            depthAttach = new TextureAttachment(GL14.GL_DEPTH_COMPONENT24);
        } else {
            depthAttach = new RenderBufferAttachment(GL14.GL_DEPTH_COMPONENT24);
        }
        return Fbo.newFbo(width, height).addColourAttachment(0, colourAttach).addDepthAttachment(depthAttach).init();
    }

}
