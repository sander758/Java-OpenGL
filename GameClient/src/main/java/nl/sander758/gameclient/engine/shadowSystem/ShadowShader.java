package nl.sander758.gameclient.engine.shadowSystem;

import nl.sander758.gameclient.engine.shaders.ShaderProgram;
import nl.sander758.gameclient.engine.shaders.UniformMatrix;

public class ShadowShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/shadowSystem/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "shaders/shadowSystem/fragmentShader.glsl";
	
	public UniformMatrix mvpMatrix = new UniformMatrix("mvpMatrix");

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);

		super.initializeUniformLocations(
				mvpMatrix
		);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
	}

}
