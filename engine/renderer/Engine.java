package renderer;

import fbos.Attachment;
import fbos.Fbo;
import fbos.RenderBufferAttachment;
import fbos.TextureAttachment;
import loader.SceneLoader;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scene.Camera;
import scene.Light;
import guis.GuiRenderer;
import guis.GuiTexture;
import loader.GLLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import scene.Scene;
import shadows.ShadowMapMasterRenderer;
import utils.DisplayManager;
import utils.OpenGlUtils;
import water.WaterTile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine {

    private static final float REFRACT_OFFSET = 1f;
    private static final float REFLECT_OFFSET = 0.1f;

    private final MasterRenderer masterRenderer;
    private Camera camera;
    private Light light;
    private Scene scene;
    private List<GuiTexture> guiTextures = new ArrayList<>();
    private GuiRenderer guiRenderer;
    private ShadowMapMasterRenderer shadowMapRenderer;


    public Engine() {
        DisplayManager.init();

        SceneLoader sceneLoader = new SceneLoader();

        scene = sceneLoader.loadScene(Paths.get(System.getProperty("user.dir") + "\\res\\save\\myscene.txt"));

        camera = new Camera(new Vector3f(0, 20, 0));

        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
        masterRenderer = new MasterRenderer(shadowMapRenderer);
        light = new Light(new Vector3f(0.4f, -0.8f, 0.2f), new Vector3f(1f, 1f, 1f), new Vector2f(0.3f, 0.8f));

        guiRenderer = new GuiRenderer();
//        guiTextures.add(new GuiTexture(shadowMapRenderer.getShadowMap(), new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));
//        guiTextures.add(new GuiTexture(2, new Vector2f(-0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));
//        guiTextures.add(new GuiTexture(3, new Vector2f(0.70f, 0.70f), new Vector2f(0.25f, 0.25f)));


        OpenGlUtils.cullBackFaces(true);
    }

    public void update() {
        camera.move(scene.getTerrains());

        masterRenderer.renderShadowMap(scene.getEntities(), light.getDirection());

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        masterRenderer.doReflectionPass(scene, camera, light, new Vector4f(0, 1, 0, -WaterTile.WATER_HEIGHT + REFLECT_OFFSET));
        masterRenderer.doRefractionPass(scene, camera, light, new Vector4f(0, -1, 0, WaterTile.WATER_HEIGHT + REFRACT_OFFSET));
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

        masterRenderer.render(scene, camera, light, new Vector4f(0, 0, 0, 0));
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
