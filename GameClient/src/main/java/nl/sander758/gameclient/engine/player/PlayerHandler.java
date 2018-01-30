package nl.sander758.gameclient.engine.player;

public class PlayerHandler {

    private static PlayerHandler handler = new PlayerHandler();

    private static Player player;

    private PlayerHandler() { }

    public static PlayerHandler getHandler() {
        return handler;
    }

    public PlayerHandler createPlayer() {
        player = new Player();
        return this;
    }

    public Player getPlayer() {
        return player;
    }
}
