package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;

public class SocketUpdater extends SocketRunnable {

    private static int TICKS_PER_SECOND = 2;

    public SocketUpdater(SocketClient client) {
        super(client);
    }

    @Override
    public void run() {
        float secondsPerUpdate = 1f / TICKS_PER_SECOND;
        long previous = getTime();
        float steps = 0.0f;
        Logger.info("Seconds per update: " + secondsPerUpdate);

        while (client.isRunning()) {
            long startTime = getTime();
            float elapsedSeconds = (startTime - previous) / 1000f;
            previous = startTime;
            steps += elapsedSeconds;
            Logger.info("Steps: " + steps);

//            while (steps >= secondsPerUpdate) {
//                Logger.debug("update game state: " + getTime());
//                steps -= secondsPerUpdate;
//            }

            Logger.debug("render: " + getTime());

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
