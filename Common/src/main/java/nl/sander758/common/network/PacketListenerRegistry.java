package nl.sander758.common.network;

import java.util.ArrayList;
import java.util.HashMap;

public class PacketListenerRegistry {

    private static HashMap<Packet.PacketType, ArrayList<PacketListener>> listeners = new HashMap<>();

    public static void register(Packet.PacketType type, PacketListener listener) {
        if (!listeners.containsKey(type)) {
            listeners.put(type, new ArrayList<>());
        }
        listeners.get(type).add(listener);
    }

    public static ArrayList<PacketListener> getListeners(Packet.PacketType type) {
        if (!listeners.containsKey(type)) {
            listeners.put(type, new ArrayList<>());
        }
        return listeners.get(type);
    }

    public static void callListeners(Packet.PacketType type, Packet packet) {
        ArrayList<PacketListener> listeners = getListeners(type);
        for (PacketListener listener : listeners) {
            listener.handle(packet);
        }
    }
}
