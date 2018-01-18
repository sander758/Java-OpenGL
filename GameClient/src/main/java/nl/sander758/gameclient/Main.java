package nl.sander758.gameclient;

import nl.sander758.gameclient.client.GameClient;
import org.lwjgl.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        new Thread(new GameClient()).start();

//        Matrix4f matrix = Maths.createTransformationMatrix(new Vector3f(0, 0, -5), new Vector3f(67, 0, 0), 1);
//        System.out.println(matrix);
    }
}
