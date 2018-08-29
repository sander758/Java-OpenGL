package nl.sander758.gameclient.network;

import nl.sander758.common.logger.Logger;
import nl.sander758.common.network.*;
import nl.sander758.common.network.packetsIn.DisconnectPacketIn;
import nl.sander758.common.network.packetsIn.PongPacketIn;
import nl.sander758.common.network.packetsOut.DisconnectPacketOut;
import nl.sander758.gameclient.engine.guiSystem.chat.ChatManager;
import nl.sander758.gameclient.engine.loader.ModelNotFoundException;
import nl.sander758.gameclient.engine.player.PlayerHandler;
import nl.sander758.gameclient.network.packetsIn.AcceptRegisterPacketIn;
import nl.sander758.gameclient.network.packetsIn.ChatMessagePacketIn;
import nl.sander758.gameclient.network.packetsIn.PlayersLocationPacketIn;
import nl.sander758.gameclient.network.packetsIn.RemovePlayerPacketIn;

import java.io.*;

class SocketListener extends SocketRunnable {

    private SocketClient client;

    public SocketListener(SocketClient client) {
        super(client.getSocket());
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (client.isRunning()) {
                int inputLength = input.readInt();
                byte[] inputData = new byte[inputLength];
                int readBytes = input.read(inputData);

                DataDeserializer deserializer = new DataDeserializer(inputData);
                byte packetId = deserializer.readUnsignedByte();

                PacketType type = PacketType.getById(packetId);

                if (type == null) {
                    Logger.error("Invalid packed received from socket: " + packetId);
                    continue;
                }

                switch (type) {
                    case ACCEPT_REGISTER:
                        AcceptRegisterPacketIn acceptRegister = new AcceptRegisterPacketIn();
                        acceptRegister.deserialize(deserializer);
                        try {
                            PlayerHandler.createPlayablePlayer(acceptRegister.getClientId());
                        } catch (ModelNotFoundException e) {
                            e.printStackTrace();
                        }
                        client.register();
                        break;

                    case DISCONNECT_PACKET:
                        Logger.debug("Disconnect packet received");
                        DisconnectPacketIn disconnectPacket = new DisconnectPacketIn();
                        disconnectPacket.deserialize(deserializer);
                        if (disconnectPacket.shouldPingBack()) {
                            trySend(new DisconnectPacketOut(false));
                        }
                        client.close();
                        break;

                    case PLAYERS_LOCATION_PACKET:
                        PlayersLocationPacketIn playersLocations = new PlayersLocationPacketIn();
                        playersLocations.deserialize(deserializer);
                        PlayerHandler.updateServerPlayers(playersLocations.getPlayersLocations());
                        break;

                    case REMOVE_PLAYER:
                        RemovePlayerPacketIn removePlayer = new RemovePlayerPacketIn();
                        removePlayer.deserialize(deserializer);
                        PlayerHandler.removeServerPlayer(removePlayer.getClientId());
                        break;

                    case PONG_PACKET:
                        PongPacketIn pongPacket = new PongPacketIn();
                        pongPacket.deserialize(deserializer);
                        PingManager.getManager().callback(pongPacket.getData());
                        break;

                    case SERVER_CHAT_PACKET:
                        ChatMessagePacketIn chatPacket = new ChatMessagePacketIn();
                        chatPacket.deserialize(deserializer);
                        ChatManager.getManager().onServerMessage(chatPacket);
                        break;
                }
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
