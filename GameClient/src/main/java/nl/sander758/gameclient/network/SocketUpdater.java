package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.SocketRunnable;
import nl.sander758.gameclient.network.packets.PlayerMovePacketOut;
import nl.sander758.gameclient.engine.player.PlayerHandler;

public class SocketUpdater extends SocketRunnable {

    private static int TICKS_PER_SECOND = 20;
    private PlayerHandler playerHandler = PlayerHandler.getHandler();

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
            long startTime = getTime();

            Logger.debug("update game state: " + getTime());
            if (playerHandler.getPlayer() != null) {
                trySend(new PlayerMovePacketOut(playerHandler.getPlayer().getCamera().getNewLocation()));
            }

            sync(startTime);
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
