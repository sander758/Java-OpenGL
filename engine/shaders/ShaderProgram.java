package shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public abstract class ShaderProgram {
    private int programID;
    private int vertexShaderID;
    private int geometryShaderID;
    private int fragmentShaderID;

    private boolean useGeometry = true;

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    protected abstract void initializeUniformLocations();

    protected abstract void bindAttributes();

    public ShaderProgram(String vertexFile, String geometryFile, String fragmentFile) {
        this.useGeometry = true;

        this.vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        this.geometryShaderID = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
        this.fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        this.programID = GL20.glCreateProgram();
        GL20.glAttachShader(this.programID, this.vertexShaderID);
        GL20.glAttachShader(this.programID, this.geometryShaderID);
        GL20.glAttachShader(this.programID, this.fragmentShaderID);

        bindAttributes();

        GL20.glLinkProgram(this.programID);
        GL20.glValidateProgram(this.programID);

        initializeUniformLocations();
    }

    public ShaderProgram(String vertexFile, String fragmentFile) {
        this.useGeometry = false;

        this.vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        this.fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        this.programID = GL20.glCreateProgram();
        GL20.glAttachShader(this.programID, this.vertexShaderID);
        GL20.glAttachShader(this.programID, this.fragmentShaderID);

        bindAttributes();

        GL20.glLinkProgram(this.programID);
        GL20.glValidateProgram(this.programID);

        initializeUniformLocations();
    }

    public void start() {
        GL20.glUseProgram(this.programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        this.stop();
        GL20.glDetachShader(this.programID, this.vertexShaderID);
        if (this.useGeometry) {
            GL20.glDetachShader(this.programID, this.geometryShaderID);
        }
        GL20.glDetachShader(this.programID, this.fragmentShaderID);
        GL20.glDeleteShader(this.vertexShaderID);
        if (this.useGeometry) {
            GL20.glDeleteShader(this.geometryShaderID);
        }
        GL20.glDeleteShader(this.fragmentShaderID);
        GL20.glDeleteProgram(this.programID);
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(this.programID, attribute, variableName);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4(location, false, matrixBuffer);
    }

    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector2f vector) {
        GL20.glUniform2f(location, vector.x, vector.y);
    }

    protected void loadVector(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadVector(int location, Vector4f vector) {
        GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    protected void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    protected void loadBool(int location, boolean value) {
        GL20.glUniform1i(location, value ? 1 : 0);
    }

    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(this.programID, uniformName);
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader: " + file);
            System.exit(-1);
        }
        return shaderID;
    }
}
