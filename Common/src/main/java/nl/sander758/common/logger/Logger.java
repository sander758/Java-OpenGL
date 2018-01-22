package nl.sander758.common.logger;

public class Logger {

    private static boolean showDebug = true;

    public static void info(String... messages) {
        for (String message : messages) {
            System.out.println("[Info] " + message);
        }
    }

    public static void debug(String... messages) {
        if (!showDebug) {
            return;
        }
        for (String message : messages) {
            System.out.println("[Debug] " + message);
        }
    }

    public static void error(String... messages) {
        for (String message : messages) {
            System.out.println("[Error] " + message);
        }
        Thread.dumpStack();
    }

    public static void error(Exception e, String... messages) {
        System.out.println("[Error] " + e.getMessage());
        for (String message : messages) {
            System.out.println("[Error] " + message);
        }
        e.printStackTrace();
    }

    public static void toggleDebug() {
        showDebug = !showDebug;
    }
}
