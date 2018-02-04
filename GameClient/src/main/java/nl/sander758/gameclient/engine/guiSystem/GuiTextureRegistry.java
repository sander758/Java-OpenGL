package nl.sander758.gameclient.engine.guiSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuiTextureRegistry {
    private static List<GuiTexture> textures = new ArrayList<>();

    public static void addTexture(GuiTexture texture) {
        textures.add(texture);
    }

    public static Collection<GuiTexture> getTextures() {
        return textures;
    }
}
