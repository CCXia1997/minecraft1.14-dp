package net.minecraft.client;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.bridge.game.GameSession;

@Environment(EnvType.CLIENT)
public class ClientGameSession implements GameSession
{
    private final int playerCount;
    private final boolean remoteServer;
    private final String difficulty;
    private final String gameMode;
    private final UUID sessionId;
    
    public ClientGameSession(final ClientWorld clientWorld, final ClientPlayerEntity clientPlayerEntity, final ClientPlayNetworkHandler clientPlayNetworkHandler) {
        this.playerCount = clientPlayNetworkHandler.getPlayerList().size();
        this.remoteServer = !clientPlayNetworkHandler.getClientConnection().isLocal();
        this.difficulty = clientWorld.getDifficulty().getTranslationKey();
        final PlayerListEntry playerListEntry4 = clientPlayNetworkHandler.getPlayerListEntry(clientPlayerEntity.getUuid());
        if (playerListEntry4 != null) {
            this.gameMode = playerListEntry4.getGameMode().getName();
        }
        else {
            this.gameMode = "unknown";
        }
        this.sessionId = clientPlayNetworkHandler.getSessionId();
    }
    
    public int getPlayerCount() {
        return this.playerCount;
    }
    
    public boolean isRemoteServer() {
        return this.remoteServer;
    }
    
    public String getDifficulty() {
        return this.difficulty;
    }
    
    public String getGameMode() {
        return this.gameMode;
    }
    
    public UUID getSessionId() {
        return this.sessionId;
    }
}
