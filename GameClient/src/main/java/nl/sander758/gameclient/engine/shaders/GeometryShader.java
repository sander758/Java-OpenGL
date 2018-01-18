package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL32;

public class GeometryShader extends Shader {

    public GeometryShader(String path) {
        super(path, GL32.GL_GEOMETRY_SHADER);
    }
}

