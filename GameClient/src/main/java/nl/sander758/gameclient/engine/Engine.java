package nl.sander758.gameclient.engine;

import nl.sander758.common.logger.Logger;
import nl.sander758.gameclient.client.entities.staticEntities.SimpleTree;
import nl.sander758.gameclient.client.entities.staticEntities.Velociraptor;
import nl.sander758.gameclient.client.entities.terrainEntities.RiverlandEntity;
import nl.sander758.gameclient.engine.display.WindowManager;
import nl.sander758.gameclient.engine.entitySystem.StaticEntityRegistry;
import nl.sander758.gameclient.engine.entitySystem.StaticEntityRenderer;
import nl.sander758.gameclient.engine.fbos.Attachment;
import nl.sander758.gameclient.engine.fbos.Fbo;
import nl.sander758.gameclient.engine.fbos.RenderBufferAttachment;
import nl.sander758.gameclient.engine.fbos.TextureAttachment;
import nl.sander758.gameclient.engine.guiSystem.GuiRenderer;
import nl.sander758.gameclient.engine.input.InputManager;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import nl.sander758.gameclient.engine.loader.ModelRegistry;
import nl.sander758.gameclient.engine.player.PlayablePlayer;
import nl.sander758.gameclient.engine.player.PlayerHandler;
import nl.sander758.gameclient.engine.player.PlayerNotFoundException;
import nl.sander758.gameclient.engine.scene.Light;
import nl.sander758.gameclient.engine.terrainSystem.TerrainEntityRegistry;
import nl.sander758.gameclient.engine.terrainSystem.TerrainRenderer;
import nl.sander758.gameclient.engine.utils.Maths;
import nl.sander758.gameclient.engine.utils.OpenGlUtils;
import nl.sander758.gameclient.engine.utils.Timer;
import nl.sander758.gameclient.engine.waterSystem.WaterEntity;
import nl.sander758.gameclient.engine.waterSystem.WaterEntityRegistry;
import nl.sander758.gameclient.engine.waterSystem.WaterRenderer;
import nl.sander758.gameclient.network.PingManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Engine package responsibility is for all the rendering, loading data to openGL, shaders, fbo
 */
public class Engine {

    private Fbo reflectionFbo;
    private Fbo refractionFbo;

    private PlayablePlayer player;
    private Light light;

    private StaticEntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private WaterRenderer waterRenderer;
    private GuiRenderer guiRenderer;

    public void init() {
        WindowManager.init();
        InputManager.register();
    }

    public void start() {
        try {
            int displayWidth = WindowManager.getWidth();
            int displayHeight = WindowManager.getHeight();

            reflectionFbo = createWaterFbo(displayWidth, displayHeight, false);
            refractionFbo = createWaterFbo(displayWidth / 2, displayHeight / 2, true);

            StaticEntityRegistry.addEntity(new SimpleTree(new Vector3f(0, -1, -6)));
            StaticEntityRegistry.addEntity(new Velociraptor(new Vector3f(0, 0, 0)));
            TerrainEntityRegistry.addEntity(new RiverlandEntity(new Vector3f(0, 0, 0)));
            WaterEntityRegistry.addEntity(new WaterEntity(new Vector2f(0, 0), 16));
            player = PlayerHandler.getPlayablePlayer();

            InputManager.registerKeyboardInputListener(player);
            InputManager.registerMouseInputListener(player);
            InputManager.registerKeyboardInputListener(PingManager.getManager());

            light = new Light(new Vector3f(0.8f, -0.8f, 0.2f), new Vector3f(1f, 1f, 1f), new Vector2f(0.3f, 0.8f));

            Matrix4f projectionMatrix = Maths.createProjectionMatrix();
            entityRenderer = new StaticEntityRenderer(projectionMatrix);
            terrainRenderer = new TerrainRenderer(projectionMatrix, 30, 4096);
            waterRenderer = new WaterRenderer(projectionMatrix);
            guiRenderer = new GuiRenderer();

            loop();

            cleanUp();

            WindowManager.destroy();
        } catch (ModelNotFoundException | PlayerNotFoundException e) {
            Logger.error(e);
        }
    }

    private void loop() {
//        guiTextures.add(new GuiTexture(shadowMapMasterRenderer.getShadowMap(), new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));
//        guiTextures.add(new GuiTexture(reflectionFbo.getColourBuffer(0), new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));
//        guiTextures.add(new GuiTexture(refractionFbo.getColourBuffer(0), new Vector2f(0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));

        OpenGlUtils.enableDepthTesting(true);

        // Starts the delta timer.
        Timer.start();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(WindowManager.getWindow()) ) {
            player.update();

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            doReflectionRender(new Vector4f(0, 1, 0, -WaterRenderer.WATER_HEIGHT + WaterRenderer.REFLECT_OFFSET));
            doRefractionRender(new Vector4f(0, -1, 0, WaterRenderer.WATER_HEIGHT + WaterRenderer.REFRACT_OFFSET));
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

            doMainRender(new Vector4f(0, 0, 0, 0));
            update();
        }
    }

    private void update() {
        glfwSwapBuffers(WindowManager.getWindow()); // swap the color buffers
        glfwPollEvents(); // Poll for window events. The key keyboardInputCallback above will only be invoked during this call.
        Timer.update();
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
        float distance = 2 * (player.getLocation().y - WaterRenderer.WATER_HEIGHT);
        player.getLocation().y -= distance;
        player.invertPitch();
        prepareRender();
        entityRenderer.render(player, light, clipPlane);
        terrainRenderer.render(player, light, clipPlane, new Matrix4f(), false);
        player.getLocation().y += distance;
        player.invertPitch();
        reflectionFbo.unbindAfterRender();
    }

    private void doRefractionRender(Vector4f clipPlane) {
        refractionFbo.bindForRender(0);
        prepareRender();
        entityRenderer.render(player, light, clipPlane);
        terrainRenderer.render(player, light, clipPlane, new Matrix4f(), false);
        refractionFbo.unbindAfterRender();
    }

    private void doMainRender(Vector4f clipPlane) {
        prepareRender();

//        shadowMapMasterRenderer.render(entities, light.getLightDirection());

        entityRenderer.render(player, light, clipPlane);
        terrainRenderer.render(player, light, clipPlane, new Matrix4f(), false);

        waterRenderer.bindTextures(reflectionFbo.getColourBuffer(0), refractionFbo.getColourBuffer(0), refractionFbo.getDepthBuffer());
        waterRenderer.render(player, light);
        guiRenderer.render();
    }

    private void cleanUp() {
        entityRenderer.cleanUp();
        terrainRenderer.cleanUp();
        waterRenderer.cleanUp();
        guiRenderer.cleanUp();
        reflectionFbo.delete();
        refractionFbo.delete();
        ModelRegistry.cleanUp();
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
