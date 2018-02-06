package nl.sander758.gameserver.network.packetsOut;

import nl.sander758.common.network.DataSerializer;
import nl.sander758.common.network.PacketOut;
import nl.sander758.common.network.PacketType;

public class AcceptRegisterPacketOut extends PacketOut {

    private int clientId;

    public AcceptRegisterPacketOut(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public void serialize(DataSerializer serializer) {
        serializer.writeInt(clientId);
    }

    @Override
    public PacketType getId() {
        return PacketType.ACCEPT_REGISTER;
    }
}
