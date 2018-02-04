package nl.sander758.common.network;

import nl.sander758.common.logger.Logger;

import java.io.*;
import java.net.Socket;

public abstract class SocketRunnable implements Runnable {
    protected Socket socket;
    protected DataInputStream input;
    protected DataOutputStream output;

    protected Thread thread;

    public SocketRunnable(Socket socket) {
        this.socket = socket;
    }

    public void start() throws IOException {
        InputStream inputStream = socket.getInputStream();
        input = new DataInputStream(inputStream);

        OutputStream outputStream = socket.getOutputStream();
        output = new DataOutputStream(outputStream);

        thread = new Thread(this);
        thread.start();
    }

    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    public void trySend(PacketOut packet) {
        if (!isAlive()) {
            Logger.error("Socket connection not alive");
            return;
        }

        DataSerializer dataSerializer = new DataSerializer();
        PacketType packetType = packet.getId();
        dataSerializer.writeUnsignedByte(packetType.getId());

        packet.serialize(dataSerializer);
        byte[] data = dataSerializer.toByteArray();

        try {
            output.writeInt(data.length);
            output.write(data);
            output.flush();
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
