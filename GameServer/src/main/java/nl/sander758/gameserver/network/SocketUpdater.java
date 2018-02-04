package nl.sander758.gameserver.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.gameserver.player.PlayerHandler;
import nl.sander758.gameserver.network.packets.out.PlayersLocationPacketOut;

import java.util.Collection;

public class SocketUpdater implements Runnable {

    private static int TICKS_PER_SECOND = 20;

    private SocketServer server;

    public SocketUpdater(SocketServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            long startTime = getTime();

            Collection<ClientConnection> connections = server.getConnections().values();
            PlayersLocationPacketOut playersLocationPacketOut = new PlayersLocationPacketOut(PlayerHandler.getPlayerHandler().getPlayers());

            Logger.debug("time: " + startTime + " players: " + connections.size());
            for (ClientConnection connection : connections) {
                connection.trySend(playersLocationPacketOut);
            }

            sync(startTime);
        }
    }

    private void sync(long startTime) {
        long endTime = startTime + (1000 / TICKS_PER_SECOND);

        while (getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private long getTime() {
        return System.currentTimeMillis();
    }
}
