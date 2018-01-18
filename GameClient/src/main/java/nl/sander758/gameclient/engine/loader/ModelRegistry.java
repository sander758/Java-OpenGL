package nl.sander758.gameclient.engine.loader;

import java.util.HashMap;

public class ModelRegistry {
    private static HashMap<String, Model> modelHashMap = new HashMap<>();

    public static void register(String name, Model model) {
        if (modelHashMap.containsKey(name)) {
            System.out.println("Model name already exists");
            return;
        }
        modelHashMap.put(name, model);
    }

    public static Model getModel(String name) {
        return modelHashMap.get(name);
    }
}
