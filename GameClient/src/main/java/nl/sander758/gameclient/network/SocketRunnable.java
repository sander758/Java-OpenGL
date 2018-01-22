package nl.sander758.gameclient.network;

import java.io.*;

public abstract class SocketRunnable implements Runnable {
    protected SocketClient client;
    protected DataInputStream input;
    protected DataOutputStream output;

    protected Thread thread;

    public SocketRunnable(SocketClient client) {
        this.client = client;
    }

    public void start() throws IOException {
        InputStream inputStream = client.getSocket().getInputStream();
        input = new DataInputStream(inputStream);

        OutputStream outputStream = client.getSocket().getOutputStream();
        output = new DataOutputStream(outputStream);

        thread = new Thread(this);
        thread.start();
    }
}
