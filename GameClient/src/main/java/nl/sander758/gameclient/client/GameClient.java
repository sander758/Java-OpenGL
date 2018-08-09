package nl.sander758.gameclient.client;

import nl.sander758.gameclient.engine.Engine;
import nl.sander758.gameclient.engine.loader.fileModel.FileModel;
import nl.sander758.gameclient.engine.loader.ModelRegistry;
import nl.sander758.gameclient.engine.waterSystem.WaterModel;
import nl.sander758.gameclient.network.SocketClient;
import org.lwjgl.opengl.GL11;

public class GameClient implements Runnable {

    @Override
    public void run() {
        // initialize network stuff
        // load config file or something and pass that to the engine
        Engine engine = new Engine();
        engine.init();

        System.out.println(GL11.glGetString(GL11.GL_VERSION));

        registerModels();

        SocketClient.getClient().connect();

        engine.start();

        SocketClient.getClient().disconnect();
    }

    private void registerModels() {
        ModelRegistry.register("player", new FileModel("models/player/player.obj"));
        ModelRegistry.register("velociraptor", new FileModel("models/lp_velociraptor/lp_velociraptor.obj"));
        ModelRegistry.register("simple_tree", new FileModel("models/simpletree/simpletree.obj"));
        ModelRegistry.register("riverland", new FileModel("models/riverland/riverland.obj"));
        ModelRegistry.register("water_16", new WaterModel(16));
    }
}
