package nl.sander758.gameclient;

import nl.sander758.gameclient.client.GameClient;
import org.lwjgl.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        new Thread(new GameClient()).start();
    }
}
