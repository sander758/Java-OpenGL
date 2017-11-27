package renderer;

import loader.SceneLoader;
import scene.Camera;
import scene.Light;
import guis.GuiRenderer;
import guis.GuiTexture;
import loader.GLLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import scene.Scene;
import fbo.FBO;
import shadows.ShadowMapMasterRenderer;
import utils.DisplayManager;

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

        SceneLoader sceneLoader = new SceneLoader();

        scene = sceneLoader.loadScene(Paths.get(System.getProperty("user.dir") + "\\res\\save\\myscene.txt"));

        camera = new Camera(new Vector3f(128, 20, 0));

        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
        masterRenderer = new MasterRenderer(shadowMapRenderer);
        light = new Light(new Vector3f(0.4f, -0.8f, 0.2f), new Vector3f(1f, 1f, 1f));

        guiRenderer = new GuiRenderer();
//        guiTextures.add(new GuiTexture(shadowMapRenderer.getShadowMap(), new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));


        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void update() {
        camera.move(scene.getTerrains());

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
