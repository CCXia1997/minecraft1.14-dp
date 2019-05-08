package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.GameMode;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int playerEntityId;
    private boolean hardcore;
    private GameMode gameMode;
    private DimensionType dimension;
    private int maxPlayers;
    private LevelGeneratorType generatorType;
    private int chunkLoadDistance;
    private boolean reducedDebugInfo;
    
    public GameJoinS2CPacket() {
    }
    
    public GameJoinS2CPacket(final int playerEntityId, final GameMode gameMode, final boolean hardcore, final DimensionType dimension, final int maxPlayers, final LevelGeneratorType generatorType, final int chunkLoadDistance, final boolean reducedDebugInfo) {
        this.playerEntityId = playerEntityId;
        this.dimension = dimension;
        this.gameMode = gameMode;
        this.maxPlayers = maxPlayers;
        this.hardcore = hardcore;
        this.generatorType = generatorType;
        this.chunkLoadDistance = chunkLoadDistance;
        this.reducedDebugInfo = reducedDebugInfo;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.playerEntityId = buf.readInt();
        int integer2 = buf.readUnsignedByte();
        this.hardcore = ((integer2 & 0x8) == 0x8);
        integer2 &= 0xFFFFFFF7;
        this.gameMode = GameMode.byId(integer2);
        this.dimension = DimensionType.byRawId(buf.readInt());
        this.maxPlayers = buf.readUnsignedByte();
        this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
        if (this.generatorType == null) {
            this.generatorType = LevelGeneratorType.DEFAULT;
        }
        this.chunkLoadDistance = buf.readVarInt();
        this.reducedDebugInfo = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.playerEntityId);
        int integer2 = this.gameMode.getId();
        if (this.hardcore) {
            integer2 |= 0x8;
        }
        buf.writeByte(integer2);
        buf.writeInt(this.dimension.getRawId());
        buf.writeByte(this.maxPlayers);
        buf.writeString(this.generatorType.getName());
        buf.writeVarInt(this.chunkLoadDistance);
        buf.writeBoolean(this.reducedDebugInfo);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onGameJoin(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityId() {
        return this.playerEntityId;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isHardcore() {
        return this.hardcore;
    }
    
    @Environment(EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }
    
    @Environment(EnvType.CLIENT)
    public DimensionType getDimension() {
        return this.dimension;
    }
    
    @Environment(EnvType.CLIENT)
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }
    
    @Environment(EnvType.CLIENT)
    public int getChunkLoadDistance() {
        return this.chunkLoadDistance;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasReducedDebugInfo() {
        return this.reducedDebugInfo;
    }
}
