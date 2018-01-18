package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL20;

public class VertexShader extends Shader {

    public VertexShader(String path) {
        super(path, GL20.GL_VERTEX_SHADER);
    }
}
