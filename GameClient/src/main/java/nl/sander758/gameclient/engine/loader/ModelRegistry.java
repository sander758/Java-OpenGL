package nl.sander758.gameclient.engine.loader;

import nl.sander758.common.logger.Logger;

import java.util.HashMap;

public class ModelRegistry {
    private static HashMap<String, Model> modelHashMap = new HashMap<>();

    public static void register(String name, Model model) {
        if (modelHashMap.containsKey(name)) {
            System.out.println("Model name already exists");
            return;
        }
        try {
            model.load();
            modelHashMap.put(name, model);
        } catch (ModelLoadingException e) {
            Logger.error(e);
        }
    }

    public static Model getModel(String name) throws ModelNotFoundException {
        if (!modelHashMap.containsKey(name)) {
            throw new ModelNotFoundException("Model: " + name + " not found in model registry");
        }
        return modelHashMap.get(name);
    }

    public static void cleanUp() {
        for (Model model : modelHashMap.values()) {
            model.cleanUp();
        }
    }
}
