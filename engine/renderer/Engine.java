package renderer;

import entities.Camera;
import entities.Entity;
import entities.EntityManager;
import entities.Light;
import guis.GuiRenderer;
import guis.GuiTexture;
import loader.GLLoader;
import loader.ModelLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import scene.Scene;
import fbo.FBO;
import shadows.ShadowMapMasterRenderer;
import terrains.Terrain;
import utils.DisplayManager;
import utils.Maths;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine {

    private final MasterRenderer masterRenderer;
    private Camera camera;
    private Light light;

    private Scene scene;

    private List<GuiTexture> guiTextures = new ArrayList<>();

    private FBO fbo;

    private GuiRenderer guiRenderer;

    private ShadowMapMasterRenderer shadowMapRenderer;

    public Engine() {
        DisplayManager.init();

        scene = ModelLoader.getModelLoader().loadScene(Paths.get(System.getProperty("user.dir") + "\\res\\save\\myscene.txt"));

        camera = new Camera(new Vector3f(0, 20, 0));

        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
        masterRenderer = new MasterRenderer(shadowMapRenderer);
        light = new Light(new Vector3f(-0.8f, -1f, 0f), new Vector3f(1f, 1f, 1f));

        guiRenderer = new GuiRenderer();

        fbo = new FBO();
//        guiTextures.add(new GuiTexture(fbo.getColorTexture(), new Vector2f(0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));

//        guiTextures.add(new GuiTexture(shadowMapRenderer.getShadowMap(), new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));


    }

    public void update() {
        camera.move(scene.getTerrains());

        fbo.bindFrameBuffer();
        masterRenderer.render(scene, camera, light, shadowMapRenderer.getToShadowMapSpaceMatrix());
        fbo.unbindCurrentFrameBuffer();


        masterRenderer.renderShadowMap(scene.getEntities(), light.getDirection());

        masterRenderer.render(scene, camera, light, shadowMapRenderer.getToShadowMapSpaceMatrix());
        guiRenderer.render(guiTextures);

        DisplayManager.update();
    }

    public void cleanUp() {
        masterRenderer.cleanUp();
        guiRenderer.cleanUp();
        GLLoader.getLoader().cleanUp();
        shadowMapRenderer.cleanUp();

        DisplayManager.closeDisplay();
    }
}
