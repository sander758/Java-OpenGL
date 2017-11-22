package shadows;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "engine/shadows/shadowVertexShader.txt";
	private static final String FRAGMENT_FILE = "engine/shadows/shadowFragmentShader.txt";
	
	private int location_mvpMatrix;

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void initializeUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
	}

}
