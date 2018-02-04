package nl.sander758.gameclient.engine.loader;

public abstract class GeneratedModel extends Model {

    public abstract void generate();

    @Override
    protected void load() {
        generate();
    }
}
