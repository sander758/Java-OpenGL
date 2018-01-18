package nl.sander758.gameclient.engine.shaders;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {

    private String name;
    private int location;

    protected Uniform(String name) {
        this.name = name;
    }

    public void storeUniformLocation(int programID) {
        location = GL20.glGetUniformLocation(programID, name);
        if (location == -1) {
            System.err.println("Invalid uniform name or unused uniform: " + name);
            Thread.dumpStack();
        }
    }

    public int getLocation() {
        return location;
    }
}
