package nl.sander758.gameclient.engine.shaders;

import nl.sander758.common.logger.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

abstract class Shader {

    private final int shaderID;

    Shader(String path, int type) {
        Logger.debug("try loading shader: " + path);

        StringBuilder shaderSource = new StringBuilder();
        try {
            ClassLoader loader = Shader.class.getClassLoader();
            InputStream inputStream = loader.getResourceAsStream(path);
            if (inputStream == null) {
                throw new IOException("Invalid shader path");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            Logger.error(e);
        }

        shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Logger.error(GL20.glGetShaderInfoLog(shaderID, 500), "Could not compile shader: " + path);
        }
    }

    int getShaderID() {
        return shaderID;
    }
}
