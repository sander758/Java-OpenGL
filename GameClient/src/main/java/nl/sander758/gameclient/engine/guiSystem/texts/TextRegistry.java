package nl.sander758.gameclient.engine.guiSystem.texts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextRegistry {

    public static HashMap<FontType, List<GuiText>> texts = new HashMap<>();

    public static HashMap<FontType, List<GuiText>> getTexts() {
        return texts;
    }

    public static void addText(GuiText text) {
        if (!texts.containsKey(text.getFontType())) {
            texts.put(text.getFontType(), new ArrayList<>());
        }
        texts.get(text.getFontType()).add(text);
    }

}
