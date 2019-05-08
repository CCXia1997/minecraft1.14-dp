package net.minecraft.server.world;

import java.util.stream.Stream;
import com.google.common.collect.Sets;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Set;

final class PlayerChunkWatchingManager
{
    private final Set<ServerPlayerEntity> watchingPlayers;
    private final Set<ServerPlayerEntity> notWatchingPlayers;
    
    PlayerChunkWatchingManager() {
        this.watchingPlayers = Sets.newHashSet();
        this.notWatchingPlayers = Sets.newHashSet();
    }
    
    public Stream<ServerPlayerEntity> getPlayersWatchingChunk(final long long1) {
        return this.watchingPlayers.stream();
    }
    
    public void add(final long long1, final ServerPlayerEntity serverPlayerEntity, final boolean watchDisabled) {
        (watchDisabled ? this.notWatchingPlayers : this.watchingPlayers).add(serverPlayerEntity);
    }
    
    public void remove(final long long1, final ServerPlayerEntity serverPlayerEntity3) {
        this.watchingPlayers.remove(serverPlayerEntity3);
        this.notWatchingPlayers.remove(serverPlayerEntity3);
    }
    
    public void disableWatch(final ServerPlayerEntity serverPlayerEntity) {
        this.notWatchingPlayers.add(serverPlayerEntity);
        this.watchingPlayers.remove(serverPlayerEntity);
    }
    
    public void enableWatch(final ServerPlayerEntity serverPlayerEntity) {
        this.notWatchingPlayers.remove(serverPlayerEntity);
        this.watchingPlayers.add(serverPlayerEntity);
    }
    
    public boolean isWatchDisabled(final ServerPlayerEntity serverPlayerEntity) {
        return !this.watchingPlayers.contains(serverPlayerEntity);
    }
    
    public void movePlayer(final long prevPos, final long currentPos, final ServerPlayerEntity serverPlayerEntity5) {
    }
}
