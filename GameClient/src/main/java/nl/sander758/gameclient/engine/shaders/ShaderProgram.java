package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL20;

public abstract class ShaderProgram {
    private final int programID;
    private VertexShader vertexShader;
    private GeometryShader geometryShader;
    private FragmentShader fragmentShader;

    private boolean useGeometry = true;

    public ShaderProgram(String vertexFile, String geometryFile, String fragmentFile) {
        useGeometry = true;

        vertexShader = new VertexShader(vertexFile);
        geometryShader = new GeometryShader(geometryFile);
        fragmentShader = new FragmentShader(fragmentFile);

        programID = GL20.glCreateProgram();

        GL20.glAttachShader(programID, vertexShader.getShaderID());
        GL20.glAttachShader(programID, geometryShader.getShaderID());
        GL20.glAttachShader(programID, fragmentShader.getShaderID());

        bindAttributes();

        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
    }

    public ShaderProgram(String vertexFile, String fragmentFile) {
        useGeometry = false;

        vertexShader = new VertexShader(vertexFile);
        fragmentShader = new FragmentShader(fragmentFile);

        programID = GL20.glCreateProgram();

        GL20.glAttachShader(programID, vertexShader.getShaderID());
        GL20.glAttachShader(programID, fragmentShader.getShaderID());

        bindAttributes();

        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
    }
    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public int getProgramID() {
        return programID;
    }

    public void cleanUp() {
        stop();

        GL20.glDetachShader(programID, vertexShader.getShaderID());
        if (useGeometry) {
            GL20.glDetachShader(programID, geometryShader.getShaderID());
        }
        GL20.glDetachShader(programID, fragmentShader.getShaderID());
        GL20.glDeleteShader(vertexShader.getShaderID());
        if (useGeometry) {
            GL20.glDeleteShader(geometryShader.getShaderID());
        }
        GL20.glDeleteShader(fragmentShader.getShaderID());

        GL20.glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    protected void initializeUniformLocations(Uniform... uniforms) {
        for (Uniform uniform : uniforms) {
            uniform.storeUniformLocation(programID);
        }
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }
}
