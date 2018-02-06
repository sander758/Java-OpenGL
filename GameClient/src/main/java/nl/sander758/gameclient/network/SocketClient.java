package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.packets.DisconnectPacketOut;
import nl.sander758.gameclient.network.packetsOut.ClientRegisterPacketOut;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.NotYetConnectedException;

public class SocketClient {

    private static SocketClient client = new SocketClient();

    private final String address;
    private final int port;

    private SocketListener socketListener;
    private SocketUpdater socketUpdater;

    private Socket socket;
    private boolean isRunning;
    private boolean registered = false;


    private SocketClient() {
        this.address = "127.0.0.1";
        this.port = 9000;
    }

    public static SocketClient getClient() {
        return client;
    }

    public void connect() {
        try {
            if (socketListener != null) {
                Logger.error("Connection instance already exists");
                return;
            }
            socket = new Socket(address, port);
            isRunning = true;

            socketListener = new SocketListener(this);
            socketListener.start();

            socketListener.trySend(new ClientRegisterPacketOut());

            // TODO update this with a notify
            // https://stackoverflow.com/questions/5999100/is-there-a-block-until-condition-becomes-true-function-in-java
            while (!registered) {
                Thread.sleep(50);
            }

            socketUpdater = new SocketUpdater(this);
            socketUpdater.start();

        } catch (IOException | InterruptedException e) {
            Logger.error(e);
        }
    }

    public void register() {
        registered = true;
    }

    public Socket getSocket() {
        if (socket == null) {
            throw new NotYetConnectedException();
        }
        return socket;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void close() {
        try {
            isRunning = false;

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socketListener.isAlive()) {
                socket.close();
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    public void disconnect() {
        socketListener.trySend(new DisconnectPacketOut(true));
    }
}
