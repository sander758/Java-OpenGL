package nl.sander758.gameclient.client;

import nl.sander758.gameclient.client.models.Riverland;
import nl.sander758.gameclient.client.models.SimpleTree;
import nl.sander758.gameclient.client.models.Velociraptor;
import nl.sander758.gameclient.engine.Engine;
import nl.sander758.gameclient.engine.loader.ModelRegistry;

public class GameClient implements Runnable {

    @Override
    public void run() {
        // initialize network stuff
        // load config file or something and pass that to the engine
        Engine engine = new Engine();
        engine.init();

        registerModels();

        engine.start();
    }

    private void registerModels() {
        ModelRegistry.register("velociraptor", new Velociraptor());
        ModelRegistry.register("simple_tree", new SimpleTree());
        ModelRegistry.register("riverland", new Riverland());
    }
}
