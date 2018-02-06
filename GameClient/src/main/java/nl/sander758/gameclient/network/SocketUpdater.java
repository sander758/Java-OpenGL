package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.SocketRunnable;
import nl.sander758.gameclient.engine.player.PlayerHandler;
import nl.sander758.gameclient.engine.player.PlayerNotFoundException;
import nl.sander758.gameclient.network.packetsOut.PlayerMovePacketOut;

public class SocketUpdater extends SocketRunnable {

    private static int TICKS_PER_SECOND = 20;

    private SocketClient client;

    public SocketUpdater(SocketClient client) {
        super(client.getSocket());
        this.client = client;
    }

    @Override
    public void run() {
        float secondsPerUpdate = 1f / TICKS_PER_SECOND;
        Logger.info("Seconds per update: " + secondsPerUpdate);

        while (client.isRunning()) {
            try {
                long startTime = getTime();

                if (PlayerHandler.getPlayablePlayer() != null) {
                    trySend(new PlayerMovePacketOut(PlayerHandler.getPlayablePlayer().getLocation()));
                }

                sync(startTime);
            } catch (PlayerNotFoundException e) {
                Logger.error(e);
            }

        }
    }

    private long getTime() {
        return System.currentTimeMillis();
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
}
