package nl.sander758.gameclient.engine.guiSystem.texts;

import nl.sander758.gameclient.engine.loader.Texture;
import nl.sander758.gameclient.engine.loader.TextureLoader;

import java.util.HashMap;
import java.util.List;

public class FontStyle {

    private String name;
    private Texture fontAtlas;

    private List<String> content;

    private int[] padding;

    private int spaceWidth;

    private int imageWidth;
    private int imageHeight;

    private int lineHeight;

    private HashMap<Integer, FontCharacter> characters = new HashMap<>();

    public FontStyle(String name, List<String> content) {
        this.name = name;
        this.content = content;

        fontAtlas = TextureLoader.getTextureLoader().loadTexture(name);

        loadInfo();
        loadCommon();
        loadCharacterData();
    }

    public String getName() {
        return name;
    }

    public Texture getFontAtlas() {
        return fontAtlas;
    }

    public FontCharacter getCharacter(int id) {
        return characters.get(id);
    }

    public int getSpaceWidth() {
        return spaceWidth;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    private void loadInfo() {
        for (String line : content) {
            if (!line.startsWith("info ")) {
                continue;
            }
            String[] splitLine = line.split(" ");
            for (String item : splitLine) {
                String[] keyValue = item.split("=");
                if (keyValue.length != 2) {
                    continue;
                }

                if (keyValue[0].equalsIgnoreCase("padding")) {
                    String[] padding = keyValue[1].split(",");
                    if (padding.length == 4) {
                        this.padding = new int[4];
                        this.padding[0] = Integer.parseInt(padding[0]);
                        this.padding[1] = Integer.parseInt(padding[1]);
                        this.padding[2] = Integer.parseInt(padding[2]);
                        this.padding[3] = Integer.parseInt(padding[3]);
                        return;
                    }
                }
            }
        }
    }

    private void loadCommon() {
        for (String line : content) {
            if (!line.startsWith("common ")) {
                continue;
            }
            String[] splitLine = line.split(" ");
            for (String item : splitLine) {
                String[] keyValue = item.split("=");
                if (keyValue.length != 2) {
                    continue;
                }

                if (keyValue[0].equalsIgnoreCase("scaleH")) {
                    imageHeight = Integer.parseInt(keyValue[1]);
                } else if (keyValue[0].equalsIgnoreCase("scaleW")) {
                    imageWidth = Integer.parseInt(keyValue[1]);
                } else if (keyValue[0].equalsIgnoreCase("lineHeight")) {
                    lineHeight = Integer.parseInt(keyValue[1]);
                }
            }
        }
    }

    private void loadCharacterData() {
        for (String line : content) {
            if (!line.startsWith("char ")) {
                continue;
            }
            String[] splitLine = line.split(" ");
            HashMap<String, Integer> characterKeyValues = new HashMap<>();
            for (String item : splitLine) {
                String[] keyValue = item.split("=");
                if (keyValue.length == 2) {
                    characterKeyValues.put(keyValue[0], Integer.parseInt(keyValue[1]));
                }
            }
            loadCharacter(characterKeyValues);
        }
    }

    private void loadCharacter(HashMap<String, Integer> characterData) {
        if (!validateCharacterData(characterData)) {
            return;
        }
        if (characterData.get("id") == TextFactory.ASCII_SPACE) {
            spaceWidth = characterData.get("xadvance");
            return;
        }
        characters.put(characterData.get("id"), new FontCharacter(
                characterData.get("id"),
                characterData.get("x"),
                characterData.get("y"),
                characterData.get("width"),
                characterData.get("height"),
                characterData.get("xadvance")
        ));
    }

    private boolean validateCharacterData(HashMap<String, Integer> characterData) {
        String[] requiredKeys = { "id", "x", "y", "width", "height", "xoffset", "yoffset", "xadvance" };
        for (String requiredKey : requiredKeys) {
            if (!characterData.containsKey(requiredKey)) {
                return false;
            }
        }
        return true;
    }
}
