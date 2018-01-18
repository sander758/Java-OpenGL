package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL20;

public class FragmentShader extends Shader {

    public FragmentShader(String path) {
        super(path, GL20.GL_FRAGMENT_SHADER);
    }
}
